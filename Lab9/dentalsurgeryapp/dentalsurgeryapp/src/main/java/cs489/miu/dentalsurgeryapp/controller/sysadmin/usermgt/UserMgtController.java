package cs489.miu.dentalsurgeryapp.controller.sysadmin.usermgt;

import cs489.miu.dentalsurgeryapp.model.User;
import cs489.miu.dentalsurgeryapp.service.RoleService;
import cs489.miu.dentalsurgeryapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@PreAuthorize("hasAuthority('ADMIN')")
public class UserMgtController {
    
    // Constants
    private static final String ROLES_ATTRIBUTE = "roles";
    private static final String USER_LIST_VIEW = "secured/sysadmin/usermgt/list";
    private static final String NEW_USER_VIEW = "secured/sysadmin/usermgt/newuser";
    private static final String EDIT_USER_VIEW = "secured/sysadmin/usermgt/edituser";
    private static final String REDIRECT_USER_LIST = "redirect:/dentalsurgeryapp/secured/sysadmin/usermgt/list";
    
    private final UserService userService;
    private final RoleService roleService;

    public UserMgtController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/dentalsurgeryapp/secured/sysadmin/usermgt/list")
    public ModelAndView displayUsersList() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("users", userService.getAllUsers());
        mav.setViewName(USER_LIST_VIEW);
        return mav;
    }

    @GetMapping("/dentalsurgeryapp/secured/sysadmin/usermgt/user/new")
    public ModelAndView displayNewUserForm() {
        ModelAndView mav = new ModelAndView();
        User user = new User();
        // Set default values for UserDetails interface fields
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        
        mav.addObject("user", user);
        mav.addObject(ROLES_ATTRIBUTE, roleService.getAllRoles());
        mav.setViewName(NEW_USER_VIEW);
        return mav;
    }

    @PostMapping("/dentalsurgeryapp/secured/sysadmin/usermgt/user/new")
    public String addNewUser(@Valid @ModelAttribute("user") User user,
                             Model model, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            model.addAttribute(ROLES_ATTRIBUTE, roleService.getAllRoles());
            return NEW_USER_VIEW;
        }
        
        // Check for duplicate username or email
        if (userService.existsByUsername(user.getUsername())) {
            model.addAttribute("usernameError", "Username already exists");
            model.addAttribute(ROLES_ATTRIBUTE, roleService.getAllRoles());
            return NEW_USER_VIEW;
        }
        
        if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("emailError", "Email already exists");
            model.addAttribute(ROLES_ATTRIBUTE, roleService.getAllRoles());
            return NEW_USER_VIEW;
        }
        
        userService.saveUser(user);
        return REDIRECT_USER_LIST;
    }

    @GetMapping("/dentalsurgeryapp/secured/sysadmin/usermgt/user/edit/{userId}")
    public String editUser(@PathVariable Integer userId, Model model) {
        var userOptional = userService.getUserById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Clear password for security - user will need to enter new password if changing
            user.setPassword("");
            model.addAttribute("user", user);
            model.addAttribute(ROLES_ATTRIBUTE, roleService.getAllRoles());
            return EDIT_USER_VIEW;
        }
        return REDIRECT_USER_LIST;
    }

    @PostMapping("/dentalsurgeryapp/secured/sysadmin/usermgt/user/edit")
    public String updateUser(@Valid @ModelAttribute("user") User user,
                             Model model, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            model.addAttribute(ROLES_ATTRIBUTE, roleService.getAllRoles());
            return EDIT_USER_VIEW;
        }
        
        // Check for duplicate username or email (excluding current user)
        var existingUserByUsername = userService.getUserByUsername(user.getUsername());
        if (existingUserByUsername.isPresent() && 
            !existingUserByUsername.get().getUserId().equals(user.getUserId())) {
            model.addAttribute("usernameError", "Username already exists");
            model.addAttribute(ROLES_ATTRIBUTE, roleService.getAllRoles());
            return EDIT_USER_VIEW;
        }
        
        var existingUserByEmail = userService.getUserByEmail(user.getEmail());
        if (existingUserByEmail.isPresent() && 
            !existingUserByEmail.get().getUserId().equals(user.getUserId())) {
            model.addAttribute("emailError", "Email already exists");
            model.addAttribute(ROLES_ATTRIBUTE, roleService.getAllRoles());
            return EDIT_USER_VIEW;
        }
        
        userService.updateUser(user);
        return REDIRECT_USER_LIST;
    }

    @GetMapping("/dentalsurgeryapp/secured/sysadmin/usermgt/user/delete/{userId}")
    public String deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
        return REDIRECT_USER_LIST;
    }
}
