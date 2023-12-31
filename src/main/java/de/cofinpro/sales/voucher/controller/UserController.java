package de.cofinpro.sales.voucher.controller;

import de.cofinpro.sales.voucher.domain.*;
import de.cofinpro.sales.voucher.model.User;
import de.cofinpro.sales.voucher.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Tag(name = "User endpoints")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/user/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Create new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400",
                    description = "Validation errors",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = VoucherServiceCustomErrorMessage.class)) })
    })
    public ResponseEntity<UserDto> signup(@Valid @RequestBody User user) {
        return new ResponseEntity<>(userService.create(user), HttpStatus.CREATED);
    }

    @GetMapping("/user")
    @Operation(description = "Get list of users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(
                    responseCode = "401",
                    description = "Access denied",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = VoucherServiceCustomErrorMessage.class)) })
    })
    public ResponseEntity<List<UserDto>> fetchAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @DeleteMapping("/user/{email}")
    @Operation(description = "Delete user by given email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(
                    responseCode = "400",
                    description = "Any validation exception",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = VoucherServiceCustomErrorMessage.class)) }),
            @ApiResponse(
                    responseCode = "401",
                    description = "Access denied",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = VoucherServiceCustomErrorMessage.class)) }),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = VoucherServiceCustomErrorMessage.class)) })
    })
    public ResponseEntity<UserDeletionResponse> remove(@PathVariable String email) {
        return ResponseEntity.ok(userService.remove(email));
    }

    @PutMapping("/user/role")
    @Operation(description = "Change user role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(
                    responseCode = "400",
                    description = "Any validation exception",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = VoucherServiceCustomErrorMessage.class)) }),
            @ApiResponse(
                    responseCode = "401",
                    description = "Access denied",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = VoucherServiceCustomErrorMessage.class)) }),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = VoucherServiceCustomErrorMessage.class)) }),
            @ApiResponse(
                    responseCode = "409",
                    description = "User already has the role",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = VoucherServiceCustomErrorMessage.class)) })
    })
    public ResponseEntity<UserDto> changeRole(@Valid @RequestBody RoleChangeRequest request) {
        return ResponseEntity.ok(userService.update(request));
    }

    @PostMapping(path = "/user/changepass", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Change user password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "The passwords must be different!"),
            @ApiResponse(responseCode = "401",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = VoucherServiceCustomErrorMessage.class)) }),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = VoucherServiceCustomErrorMessage.class)) })
    })
    public ResponseEntity<PasswordChangeResponse> changePassword(@Valid @RequestBody PasswordChangeRequest request,
                                                                 @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.changePass(request, userDetails));
    }
}
