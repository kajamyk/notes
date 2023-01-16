package com.example.notes.infra.api.rest;

import com.example.notes.core.Registration;
import com.example.notes.infra.api.services.BannedUserService;
import com.example.notes.infra.api.services.UserService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class LoginController {
    @Autowired
    UserService userService;

    @Autowired
    BannedUserService bannedUserService;

    @Autowired
    ObjectFactory<HttpSession> httpSessionFactory;

    @GetMapping("/home")
    public ModelAndView getHomePage() {
        try {
            String currentIP = getCurrentUserIP();
            if (bannedUserService.isUserBanned(currentIP)) {
                ModelAndView modelAndView = new ModelAndView();
                modelAndView.addObject("errorMessage", "You are banned for eternity! \uD83D\uDE20");
                modelAndView.setViewName("loginPage");
                return modelAndView;
            }

            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("loginPage");
            return modelAndView;
        } catch (Exception e) {
            log.error(e.getMessage());
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("loginPage");
            return modelAndView;
        }
    }

    @PostMapping("/login")
    public ModelAndView login(@Valid @NonNull String userName, @Valid @NonNull String password) throws InterruptedException {
        try {
            clearHttpSession();
            userName = Jsoup.clean(userName, Safelist.basic());
            password = Jsoup.clean(password, Safelist.basic());

            if(userName.equals("admin") && password.equals("admin123")) {
                ModelAndView modelAndView = new ModelAndView();
                modelAndView.setViewName("redirect:/admin");
                return modelAndView;
            }
            HttpSession session = httpSessionFactory.getObject();
            String currentIP = getCurrentUserIP();

            TimeUnit.SECONDS.sleep(2);

            if (bannedUserService.isUserBanned(currentIP)) {
                ModelAndView modelAndView = new ModelAndView();
                modelAndView.addObject("errorMessage", "You are banned for eternity! \uD83D\uDE20");
                modelAndView.setViewName("loginPage");
                return modelAndView;
            }

            if (!userService.areUserCredentialsValid(userName, password)) {
                if (session.getAttribute(userName) == null) {
                    session.setAttribute(userName, 1);
                } else {
                    session.setAttribute(userName, (Integer) session.getAttribute(userName) + 1);
                    if ((Integer) session.getAttribute(userName) >= 3) {
                        bannedUserService.addBannedUser(currentIP);
                    }
                }

                ModelAndView modelAndView = new ModelAndView();
                modelAndView.addObject("errorMessage", "Bad credentials! \uD83D\uDE20");
                modelAndView.setViewName("loginPage");
                return modelAndView;
            }

            session.setAttribute("userName", userName);
            ModelAndView modelAndView = new ModelAndView();
            //modelAndView.addObject("notes", userService.getAllNotes(userName));
            modelAndView.setViewName("redirect:/notes");
            return modelAndView;
        } catch (Exception e) {
            log.error(e.getMessage());
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("loginPage");
            return modelAndView;
        }

    }

    @PostMapping("/register")
    public ModelAndView register(@Valid @NonNull String userName, @Valid @NonNull String password, @Valid @NonNull String repeatedPassword) {
        try {
            clearHttpSession();
            userName = Jsoup.clean(userName, Safelist.basic());
            password = Jsoup.clean(password, Safelist.basic());
            repeatedPassword = Jsoup.clean(repeatedPassword, Safelist.basic());

            String currentIP = getCurrentUserIP();
            if (bannedUserService.isUserBanned(currentIP)) {
                ModelAndView modelAndView = new ModelAndView();
                modelAndView.addObject("errorMessage", "You are banned for eternity! \uD83D\uDE20");
                modelAndView.setViewName("loginPage");
                return modelAndView;
            }

            Registration registration = new Registration();

            if (!registration.isUserNameCorrect(userName)) {
                ModelAndView modelAndView = new ModelAndView();
                modelAndView.addObject("errorMessage", "Login can only contain letters, digits and underscores!");
                modelAndView.setViewName("loginPage");
                return modelAndView;
            }
            if (!userService.isUserNameAvailable(userName)) {
                ModelAndView modelAndView = new ModelAndView();
                modelAndView.addObject("errorMessage", "Choose different login!");
                modelAndView.setViewName("loginPage");
                return modelAndView;
            }
            if (!registration.arePasswordsMatching(password, repeatedPassword)) {
                ModelAndView modelAndView = new ModelAndView();
                modelAndView.addObject("errorMessage", "Passwords are not matching!");
                modelAndView.setViewName("loginPage");
                return modelAndView;
            }
            if (!registration.isPasswordEntropySufficient(password)) {
                ModelAndView modelAndView = new ModelAndView();
                modelAndView.addObject("errorMessage", "Password is not strong enough!");
                modelAndView.setViewName("loginPage");
                return modelAndView;
            }
            if (!registration.isPasswordLengthSufficient(password)) {
                ModelAndView modelAndView = new ModelAndView();
                modelAndView.addObject("errorMessage", "Password is not long enough!");
                modelAndView.setViewName("loginPage");
                return modelAndView;
            }
            userService.addUser(userName, password);
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("errorMessage", "Now you can log in \uD83D\uDE0E");
            modelAndView.setViewName("loginPage");
            return modelAndView;
        } catch (Exception e) {
            log.error(e.getMessage());
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("loginPage");
            return modelAndView;
        }

    }

    @GetMapping("/notes/logout")
    public ModelAndView logout() {
        try {
            clearHttpSession();
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("redirect:/home");
            return modelAndView;
        } catch (Exception e) {
            log.error(e.getMessage());
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("loginPage");
            return modelAndView;
        }

    }

    @GetMapping("/admin")
    public ModelAndView getAdminPage() {
        String IP = getCurrentUserIP();
        bannedUserService.addBannedUser(IP);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("adminPage");
        return modelAndView;
    }


    private void clearHttpSession() {
        try {
            HttpSession session = httpSessionFactory.getObject();
            session.removeAttribute("userName");
        } catch (Exception ignored) {

        }
    }

    private String getCurrentUserIP() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        return request.getRemoteAddr();
    }
}
