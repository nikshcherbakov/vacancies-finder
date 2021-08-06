package com.nikshcherbakov.vacanciesfinder.services;

import com.nikshcherbakov.vacanciesfinder.models.Address;
import com.nikshcherbakov.vacanciesfinder.models.VacancyPreview;
import com.nikshcherbakov.vacanciesfinder.utils.VacancyTableRow;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class VacanciesService {

    @Value("${app.vacanciesrendering.perpage}")
    private Integer perPage;

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
            String addressString = null;
            if (vacancyAddress != null) {
                addressString = vacancyAddress.asString() != null ?
                        vacancyAddress.asString() :
                        vacancy.getArea().getName();
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

}
