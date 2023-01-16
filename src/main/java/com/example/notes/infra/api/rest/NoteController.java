package com.example.notes.infra.api.rest;

import com.example.notes.infra.api.services.NoteService;
import com.example.notes.infra.api.services.UserService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/notes")
public class NoteController {
    @Autowired
    ObjectFactory<HttpSession> httpSessionFactory;
    @Autowired
    UserService userService;
    @Autowired
    NoteService noteService;

    @PostMapping("/add")
    public ModelAndView addNote(@Valid @NonNull String note, @Valid String password) throws Exception {
        try {
            password = Jsoup.clean(password, Safelist.basic());
            note = Jsoup.clean(note, Safelist.basic());
            HttpSession session = httpSessionFactory.getObject();
            String userName = (String) session.getAttribute("userName");

            if (userService.getUserByUserName(userName) != null) {
                userService.addUserNote(userName, note, password);
            }
            return getNotesPage();
        } catch (Exception e) {
            log.error(e.getMessage());
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("notesPage");
            return modelAndView;
        }

    }

    @PostMapping("/addPublic")
    public ModelAndView addPublicNote(@Valid @NonNull String note, String password) {
        try {
            HttpSession session = httpSessionFactory.getObject();
            String userName = (String) session.getAttribute("userName");
            if (userService.getUserByUserName(userName) != null) {
                note = Jsoup.clean(note, Safelist.basic());
                noteService.addPublicNote(note);
            }
            return getNotesPage();
        } catch (Exception e) {
            log.error(e.getMessage());
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("notesPage");
            return modelAndView;
        }

    }

    @GetMapping("")
    public ModelAndView getNotesPage() {
        try {
            HttpSession session = httpSessionFactory.getObject();
            String userName = (String) session.getAttribute("userName");
            if (userService.getUserByUserName(userName) != null) {
                ModelAndView modelAndView = new ModelAndView();
                modelAndView.setViewName("notesPage");
                modelAndView.addObject("notes", userService.getAllUserNotes(userName));
                return modelAndView;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("notesPage");
            return modelAndView;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("notesPage");
        return modelAndView;
    }

    @GetMapping("/secret")
    public ModelAndView getSecretPage(@Valid @NonNull String password) {
        try {
            HttpSession session = httpSessionFactory.getObject();
            String userName = (String) session.getAttribute("userName");
            if (userService.getUserByUserName(userName) != null) {
                ModelAndView modelAndView = new ModelAndView();
                modelAndView.setViewName("secretNotePage");

                modelAndView.addObject("note", userService.getSecretNote(userName, password));
                return modelAndView;
            }
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("notesPage");
            return modelAndView;

        } catch (Exception exception) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("notesPage");
            return modelAndView;
        }
    }

    @GetMapping("/back")
    public ModelAndView getReturnPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/notes");
        return modelAndView;
    }
}
