package com.example.notes.infra.api.rest;

import com.example.notes.core.Registration;
import com.example.notes.infra.api.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class LoginController {
    @Autowired
    UserService userService;

    @Autowired
    ObjectFactory<HttpSession> httpSessionFactory;
    @GetMapping("/home")
    public ModelAndView getHomePage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("loginPage");
        return modelAndView;
    }
    @PostMapping("/login")
    public ModelAndView login(@Valid String userName, @Valid String password) throws InterruptedException {
        userName = Jsoup.clean(userName, Safelist.basic());
        password = Jsoup.clean(password, Safelist.basic());

        TimeUnit.SECONDS.sleep(2);
        if(! userService.areUserCredentialsValid(userName, password)) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("errorMessage", "Bad credentials");
            modelAndView.setViewName("loginPage");
            return modelAndView;
        }
        HttpSession session = httpSessionFactory.getObject();
        session.setAttribute("userName", userName);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("notes", userService.getAllNotes(userName));
        modelAndView.setViewName("notesPage");
        return modelAndView;
    }
    @PostMapping("/register")
    public ModelAndView register(@Valid String userName, @Valid String password, @Valid String repeatedPassword) {
        userName = Jsoup.clean(userName, Safelist.basic());
        password = Jsoup.clean(password, Safelist.basic());
        repeatedPassword = Jsoup.clean(repeatedPassword, Safelist.basic());

        Registration registration = new Registration();
        if(!userService.isUserNameAvailable(userName)) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("errorMessage", "Choose different login");
            modelAndView.setViewName("loginPage");
            return modelAndView;
        }
        if(! registration.arePasswordsMatching(password, repeatedPassword)) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("errorMessage", "Passwords are not matching!");
            modelAndView.setViewName("loginPage");
            return modelAndView;
        }
        if(! registration.isPasswordEntropySufficient(password)) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("errorMessage", "Password is not strong enough!");
            modelAndView.setViewName("loginPage");
            return modelAndView;
        }
        if(! registration.isPasswordLengthSufficient(password)) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("errorMessage", "Password is not long enough!");
            modelAndView.setViewName("loginPage");
            return modelAndView;
        }
        userService.addUser(userName, password);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("loginPage");
        return modelAndView;
    }
}
