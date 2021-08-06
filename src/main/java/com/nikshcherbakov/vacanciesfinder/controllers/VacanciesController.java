package com.nikshcherbakov.vacanciesfinder.controllers;

import com.nikshcherbakov.vacanciesfinder.models.User;
import com.nikshcherbakov.vacanciesfinder.models.VacancyPreview;
import com.nikshcherbakov.vacanciesfinder.repositories.UserRepository;
import com.nikshcherbakov.vacanciesfinder.repositories.VacancyPreviewRepository;
import com.nikshcherbakov.vacanciesfinder.services.UserService;
import com.nikshcherbakov.vacanciesfinder.services.VacanciesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class VacanciesController {

    private final VacanciesService vacanciesService;
    private final UserService userService;
    private final VacancyPreviewRepository vacancyRepository;
    private final UserRepository userRepository;

    public VacanciesController(VacanciesService vacanciesService, UserService userService,
                               VacancyPreviewRepository vacancyRepository, UserRepository userRepository) {
        this.vacanciesService = vacanciesService;
        this.userService = userService;
        this.vacancyRepository = vacancyRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/vacancies")
    public String showVacanciesPage(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        User user = userService.retrieveAuthenticatedUser();

        if (page < 0) {
            redirectAttributes.addAttribute("page", 1);
            return "redirect:/vacancies";
        }
        int userVacanciesPages = vacanciesService.pages(user.getVacancies());
        if (page > userVacanciesPages) {
            redirectAttributes.addAttribute("page", userVacanciesPages);
            return "redirect:/vacancies";
        }

        model.addAttribute("vacancies", vacanciesService.getPage(user.getVacancies(), page));
        model.addAttribute("page", page);
        model.addAttribute("nextPage", page + 1 <= userVacanciesPages? page + 1 : null);
        model.addAttribute("previousPage", page - 1 > 0? page - 1 : null);
        return "vacancies";
    }

    @GetMapping("/likeVacancy")
    public String likeVacancy(@RequestParam(name = "id") Long id, @RequestParam(name = "fromPage") Integer fromPage,
                              RedirectAttributes redirectAttributes) {
        User user = userService.retrieveAuthenticatedUser();
        Optional<VacancyPreview> vacancy = vacancyRepository.findById(id);
        if (vacancy.isPresent()) {
            user.addFavoriteVacancy(vacancy.get());
            user.removeVacancy(vacancy.get());
        }
        userRepository.save(user);
        redirectAttributes.addAttribute("page", fromPage);
        return "redirect:/vacancies";
    }

    @GetMapping("/favoriteVacancies")
    public String showFavoriteVacanciesPage(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        User user = userService.retrieveAuthenticatedUser();

        if (page < 0) {
            redirectAttributes.addAttribute("page", 1);
            return "redirect:/favoriteVacancies";
        }
        int userFavoriteVacanciesPages = vacanciesService.pages(user.getFavoriteVacancies());
        if (page > userFavoriteVacanciesPages) {
            redirectAttributes.addAttribute("page", userFavoriteVacanciesPages);
            return "redirect:/favoriteVacancies";
        }

        model.addAttribute("favoriteVacancies", vacanciesService.getPage(user.getFavoriteVacancies(), page));
        model.addAttribute("page", page);
        model.addAttribute("nextPage", page + 1 <= userFavoriteVacanciesPages? page + 1 : null);
        model.addAttribute("previousPage", page - 1 > 0? page - 1 : null);
        return "favorite-vacancies";
    }

    @GetMapping("/dislikeVacancy")
    public String dislikeVacancy(@RequestParam(name = "id") Long id, @RequestParam(name = "fromPage") Integer fromPage,
                              RedirectAttributes redirectAttributes) {
        User user = userService.retrieveAuthenticatedUser();
        Optional<VacancyPreview> favoriteVacancy = vacancyRepository.findById(id);
        favoriteVacancy.ifPresent(user::removeFavoriteVacancy);
        userRepository.save(user);
        redirectAttributes.addAttribute("page", fromPage);
        return "redirect:/favoriteVacancies";
    }

}
