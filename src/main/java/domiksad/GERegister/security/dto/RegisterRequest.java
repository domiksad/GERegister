package domiksad.GERegister.security.dto;

import domiksad.GERegister.security.entity.Role;

import java.util.UUID;

public record RegisterRequest(
    SignupRequest signupData,
    Role role,
    UUID hunterId
) {}