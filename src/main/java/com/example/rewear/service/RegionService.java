package com.example.rewear.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegionService {
    private final HttpSession session;

    public void saveAddress(String address) {
        // Store it in a session, save it in a DB, or implement it in any way you want.
        session.setAttribute("currentRegion", address);
    }

    public String getSavedAddress() {
        return (String) session.getAttribute("currentRegion");
    }
}
