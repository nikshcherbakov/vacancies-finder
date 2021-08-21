package com.nikshcherbakov.vacanciesfinder.services;

import com.nikshcherbakov.vacanciesfinder.models.*;
import com.nikshcherbakov.vacanciesfinder.repositories.VacancyPreviewRepository;
import com.nikshcherbakov.vacanciesfinder.utils.HighlightType;
import com.nikshcherbakov.vacanciesfinder.utils.VacancyTableRow;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class VacanciesService {

    @Value("${app.vacanciesrendering.perpage}")
    private Integer perPage;

    private final VacancyPreviewRepository vacancyPreviewRepository;

    private final AddressService addressService;
    private final VacancyAreaService areaService;
    private final VacancyEmployerService employerService;

    public VacanciesService(AddressService addressService, VacancyAreaService areaService,
                            VacancyEmployerService employerService, VacancyPreviewRepository vacancyPreviewRepository) {
        this.addressService = addressService;
        this.areaService = areaService;
        this.employerService = employerService;
        this.vacancyPreviewRepository = vacancyPreviewRepository;
    }

    /**
     * Returns page of vacancies depending on perPage value from
     * {@code application.properties} file.
     * @param vacancies list of vacancies to worh with
     * @param page number of page, should be positive
     * @return page of vacancies if there is at least one vacancy to be shown
     * on page, otherwise - null
     */
    public List<VacancyTableRow> getPage(@NotNull List<VacancyPreview> vacancies, @NotNull int page) {
        if (page <= 0) {
            return null;
        }
        if (vacancies == null) {
            return null;
        }

        List<VacancyTableRow> pageOfVacancies = new ArrayList<>();

        int fromInd = Math.min((page - 1) * perPage, vacancies.size());
        int toInd = Math.min(page * perPage, vacancies.size());
        for (int i = fromInd; i < toInd; i++) {
            VacancyPreview vacancy = vacancies.get(i);
            Address vacancyAddress = vacancy.getAddress();
            String addressString = vacancy.getArea().getName();
            if (vacancyAddress != null) {
                if (vacancyAddress.asString() != null) {
                    addressString = vacancyAddress.asString();
                }
            }

            String vacancyResponsibility = vacancy.getSnippet().getResponsibility();

            VacancyTableRow vacancyRow = new VacancyTableRow.Builder()
                    .withNumber(i + 1)
                    .withVacancyId(vacancy.getId())
                    .withName(vacancy.getName())
                    .withAddress(addressString)
                    .withEmployer(vacancy.getEmployer().getName())
                    .withLink(vacancy.getUrl())
                    .withResponsibility(vacancyResponsibility != null?
                            vacancyResponsibility.replaceAll("<[^>]*>", "") :
                            null) // removing html tags
                    .withSalaryFrom(vacancy.getSalary() != null? vacancy.getSalary().getFrom() : null)
                    .withSalaryTo(vacancy.getSalary() != null? vacancy.getSalary().getTo() : null)
                    .withCurrency(vacancy.getSalary() != null? vacancy.getSalary().getCurrency() : null)
                    .build();

            pageOfVacancies.add(vacancyRow);
        }

        return pageOfVacancies.size() > 0? pageOfVacancies : null;
    }

    /**
     * Returns number of pages of a user vacancies' list depending on perPage
     * parameter value from {@code application.properties} file
     * @param vacancies collection of vacancies to work with
     * @return number of pages of a user's list of vacancies, if user does not have any vacancies returns 0
     */
    public int pages(Collection<VacancyPreview> vacancies) {
        if (vacancies == null) {
            return 0;
        }
        return vacancies.size() / perPage + (vacancies.size() % perPage > 0? 1 : 0);
    }


    /**
     * Generates vacancies list description to be used as string
     * @param vacancies list of vacancies for which description is generated
     * @param highlightType type of text highlighting. Possible values:
     * {@link HighlightType#HTML_TELEGRAM}, {@link HighlightType#HTML_EMAIL},
     * {@link HighlightType#NONE}
     * @return string description of specified list of vacancies, if list of
     * vacancies is empty returns empty string ("")
     */
    public String generateVacanciesListMessage(@NotNull List<VacancyPreview> vacancies, HighlightType highlightType) {
        StringBuilder builder = new StringBuilder();
        boolean withMarkdown = false;
        switch (highlightType) {
            case HTML_EMAIL:
                builder.append("<ol>");

                for (VacancyPreview vacancy : vacancies) {
                    String vacancyDescription = String.format("\n<li>%s</li>", vacancy.getDescription(true));
                    builder.append(vacancyDescription);
                }

                builder.append("\n</ol>");
                return vacancies.size() > 0 ? builder.toString() : "";

            case HTML_TELEGRAM:
                withMarkdown = true;
                break;
            case NONE:
                withMarkdown = false;
                break;
        }

        int number = 1;
        for (VacancyPreview vacancy : vacancies) {
            String vacancyDescription = String.format("%d. %s\n", number, vacancy.getDescription(withMarkdown));
            builder.append(vacancyDescription);
            number++;
        }
        return builder.length() > 0 ? builder.substring(0, builder.length() - 1) : "";
    }

    /**
     * Deletes vacancy and associated records from the database
     * @param vacancyToBeDeleted vacancy that needs to be deleted
     * @implNote Note that the method deletes vacancy and all associated objects
     * if they are not used anymore (address, area, employer)
     */
    @Transactional
    public void removeVacancy(VacancyPreview vacancyToBeDeleted) {
        // Checking associated objects
        Address vacancyAddress = vacancyToBeDeleted.getAddress();
        if (vacancyAddress != null) {
            vacancyAddress.getVacancyPreviews().remove(vacancyToBeDeleted);
            vacancyToBeDeleted.setAddress(null);
            if (vacancyAddress.getVacancyPreviews().isEmpty()) {
                addressService.removeAddress(vacancyAddress);
            }
        }

        VacancyArea vacancyArea = vacancyToBeDeleted.getArea();
        vacancyArea.getVacancyPreviews().remove(vacancyToBeDeleted);
        vacancyToBeDeleted.setArea(null);
        if (vacancyArea.getVacancyPreviews().isEmpty()) {
            areaService.removeVacancyArea(vacancyArea);
        }

        VacancyEmployer vacancyEmployer = vacancyToBeDeleted.getEmployer();
        vacancyEmployer.getVacancyPreviews().remove(vacancyToBeDeleted);
        vacancyToBeDeleted.setEmployer(null);
        if (vacancyEmployer.getVacancyPreviews().isEmpty()) {
            employerService.removeEmployer(vacancyEmployer);
        }

        // Deleting vacancy
        vacancyPreviewRepository.delete(vacancyToBeDeleted);
    }

    /**
     * Looks for a vacancy in the database by id
     * @param id id by which a search is performed
     * @return vacancy preview if one exists in the database, otherwise null
     */
    public VacancyPreview findById(Long id) {
        Optional<VacancyPreview> vacancy = vacancyPreviewRepository.findById(id);
        return vacancy.orElse(null);
    }

    /**
     * Saves vacancy preview to the database
     * @param vacancy vacancy that needs to be saved
     */
    public void save(VacancyPreview vacancy) {
        vacancyPreviewRepository.save(vacancy);
    }
}
