package io.github.frscontiero.taskapi.auth.application;

import io.github.frscontiero.taskapi.auth.domain.AccessToken;

public record TokenPair(AccessToken accessToken, String refreshToken) {}
