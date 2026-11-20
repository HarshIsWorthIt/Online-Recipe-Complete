package com.recipely.service;

/** Immutable search input passed from the servlet layer to the service layer. */
public record RecipeFilter(String search, String nation, String taste, int maxMinutes) { }
