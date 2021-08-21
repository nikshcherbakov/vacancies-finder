package com.nikshcherbakov.vacanciesfinder.controllers;

import com.nikshcherbakov.vacanciesfinder.models.User;
import com.nikshcherbakov.vacanciesfinder.models.VacancyPreview;
import com.nikshcherbakov.vacanciesfinder.services.UserService;
import com.nikshcherbakov.vacanciesfinder.services.VacanciesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class VacanciesController {

    private final VacanciesService vacanciesService;
    private final UserService userService;

    public VacanciesController(VacanciesService vacanciesService, UserService userService) {
        this.vacanciesService = vacanciesService;
        this.userService = userService;
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

        List<VacancyPreview> userVacancies = user.getVacancies();
        int userVacanciesPages = vacanciesService.pages(userVacancies);
        if (page > userVacanciesPages) {
            redirectAttributes.addAttribute("page", userVacanciesPages);
            return "redirect:/vacancies";
        }

        model.addAttribute("vacancies", vacanciesService.getPage(userVacancies, page));
        model.addAttribute("page", page);
        model.addAttribute("nextPage", page + 1 <= userVacanciesPages? page + 1 : null);
        model.addAttribute("previousPage", page - 1 > 0? page - 1 : null);
        return "vacancies";
    }

    @GetMapping("/likeVacancy")
    public String likeVacancy(@RequestParam(name = "id") Long id, @RequestParam(name = "fromPage") Integer fromPage,
                              RedirectAttributes redirectAttributes) {
        User user = userService.retrieveAuthenticatedUser();
        VacancyPreview vacancy = vacanciesService.findById(id);
        if (vacancy != null) {
            user.likeVacancy(vacancy);
            userService.save(user);
        }
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

        List<VacancyPreview> favoriteVacancies = user.getFavoriteVacancies();
        int userFavoriteVacanciesPages = vacanciesService.pages(favoriteVacancies);
        if (page > userFavoriteVacanciesPages) {
            redirectAttributes.addAttribute("page", userFavoriteVacanciesPages);
            return "redirect:/favoriteVacancies";
        }

        model.addAttribute("favoriteVacancies", vacanciesService.getPage(favoriteVacancies, page));
        model.addAttribute("page", page);
        model.addAttribute("nextPage", page + 1 <= userFavoriteVacanciesPages? page + 1 : null);
        model.addAttribute("previousPage", page - 1 > 0 ? page - 1 : null);
        return "favorite-vacancies";
    }

    @GetMapping("/dislikeVacancy")
    public String dislikeVacancy(@RequestParam(name = "id") Long id, @RequestParam(name = "fromPage") Integer fromPage,
                                 @RequestParam(name = "redirectFrom", defaultValue = "favoriteVacancies") String redirectFrom,
                                 RedirectAttributes redirectAttributes) {
        User user = userService.retrieveAuthenticatedUser();
        VacancyPreview dislikedVacancy = vacanciesService.findById(id);
        if (dislikedVacancy != null) {
            // Removing disliked vacancy from user
            userService.removeVacancyFromUser(user, dislikedVacancy);
        }

        redirectAttributes.addAttribute("page", fromPage);
        return String.format("redirect:/%s", redirectFrom);
    }

}
