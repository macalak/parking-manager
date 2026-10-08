package com.example.parkingmanager.adapter.in.web;

import com.example.parkingmanager.application.FacilityNotFoundException;
import com.example.parkingmanager.application.CustomerDetailsService;
import com.example.parkingmanager.application.ParkingOverviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ParkingController {

    private final ParkingOverviewService parkingOverviewService;
    private final CustomerDetailsService customerDetailsService;

    public ParkingController(
            ParkingOverviewService parkingOverviewService,
            CustomerDetailsService customerDetailsService) {
        this.parkingOverviewService = parkingOverviewService;
        this.customerDetailsService = customerDetailsService;
    }

    @GetMapping("/")
    public String index(@RequestParam(name = "facilityId", required = false) String facilityId, Model model) {
        var facilities = parkingOverviewService.findFacilities();
        model.addAttribute("facilities", facilities);

        if (facilities.isEmpty()) {
            model.addAttribute("overview", null);
            return "index";
        }

        String selectedFacilityId = facilityId == null ? facilities.getFirst().locationId() : facilityId;
        if (facilities.stream().noneMatch(facility -> facility.locationId().equals(selectedFacilityId))) {
            throw new FacilityNotFoundException(selectedFacilityId);
        }
        model.addAttribute("selectedFacilityId", selectedFacilityId);
        model.addAttribute("overview", parkingOverviewService.getOverview(selectedFacilityId));
        return "index";
    }

    @GetMapping("/customers")
    public String customerDetails(
            @RequestParam(name = "businessId", required = false) String businessId,
            Model model) {
        if (businessId != null && !businessId.isBlank()) {
            var customer = customerDetailsService.getCustomer(businessId.trim());
            model.addAttribute("customer", customer);
            model.addAttribute("customerType", customer.getCustomerType() == null
                    ? null
                    : customer.getCustomerType().getValue());
            model.addAttribute("businessId", businessId.trim());
        }
        return "customer-details";
    }
}
