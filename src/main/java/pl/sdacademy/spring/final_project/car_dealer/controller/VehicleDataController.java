package pl.sdacademy.spring.final_project.car_dealer.controller;

import org.springframework.ui.Model;
import pl.sdacademy.spring.final_project.car_dealer.dto.AddVehicleDropDownListDto;
import pl.sdacademy.spring.final_project.car_dealer.model.Vehicle;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.sdacademy.spring.final_project.car_dealer.service.DefaultDictionaryService;
import pl.sdacademy.spring.final_project.car_dealer.service.VehicleDataService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/vehicles")
public class VehicleDataController {

    private VehicleDataService vehicleDataService;
    private DefaultDictionaryService defaultDictionaryService;

    public VehicleDataController(VehicleDataService vehicleDataService, DefaultDictionaryService defaultDictionaryService) {
        this.vehicleDataService = vehicleDataService;
        this.defaultDictionaryService = defaultDictionaryService;
    }

    @GetMapping
    public String showAvailableVehicles(Model model) {
        List<Vehicle> vehicles = vehicleDataService.loadAllAvailableVehicles();
        model.addAttribute("headerMsg", "Vehilces For Sale");
        model.addAttribute("vehicles", vehicles);
        return "vehicles";
    }

    @GetMapping("/sold")
    public String showAllSoldVehicles(Model model) {
        List<Vehicle> vehicles = vehicleDataService.loadAllSoldVehicles();
        model.addAttribute("headerMsg", "Sold Vehicles");
        model.addAttribute("vehicles", vehicles);
        return "vehicles";
    }

    @GetMapping("/all")
    public String showAllVehicles(Model model) {
        List<Vehicle> vehicles = vehicleDataService.loadAllVehicles();
        model.addAttribute("headerMsg", "All Vehicles");
        model.addAttribute("vehicles", vehicles);
        return "vehicles";
    }

    @GetMapping("/{id}")
    public String showVehicleDetails(Model model, @PathVariable("id") Long vehicleID) {
        Vehicle vehicle = vehicleDataService.loadVehicleById(vehicleID);
        model.addAttribute("vehicle", vehicle);
        return "vehicleDetails";
    }


    @GetMapping("/new")
    public String addVehicleForm(Model model) {
        AddVehicleDropDownListDto addVehicleDropDownListDto = getDropList();
        model.addAttribute("dropList", addVehicleDropDownListDto);
        Vehicle attributeValue = new Vehicle();
        model.addAttribute("addedVehicle", attributeValue);
        return "addVehicle";
    }

    @GetMapping("/{id}/sell")
    public String sellVehicle(@PathVariable("id") Long vehicleID, Model model) {

        Vehicle vehicle = vehicleDataService.loadVehicleById(vehicleID);
        vehicle.setSold(true);
        vehicleDataService.addVehicle(vehicle);
        return "redirect:/vehicles";
    }

    @PostMapping
    public String saveVehicle(
            @Valid @ModelAttribute("addedVehicle") Vehicle vehicleToSave,
            BindingResult bindingResult,
            Model model) {

        AddVehicleDropDownListDto dropList = getDropList();

        if (bindingResult.hasErrors()) {
            model.addAttribute("dropList", dropList);
            model.addAttribute("addedVehicle", vehicleToSave);
            return "addVehicle";
        }
        if (vehicleDataService.loadVehicleByVin(vehicleToSave.getVin()) != null) {
            bindingResult.rejectValue("vin", "vin", "Vehicle cannot be sold Twice");
            model.addAttribute("dropList", dropList);
            model.addAttribute("addedVehicle", vehicleToSave);
            return "addVehicle";
        }
        vehicleDataService.addVehicle(vehicleToSave);

        return "redirect:/vehicles";
    }

    private AddVehicleDropDownListDto getDropList() {
        AddVehicleDropDownListDto addVehicleDropDownListDto = AddVehicleDropDownListDto.getINSTANCE();
        addVehicleDropDownListDto.setFuels(defaultDictionaryService.getFuel());
        addVehicleDropDownListDto.setManufacturers(defaultDictionaryService.getManufacturer());
        addVehicleDropDownListDto.setModels(defaultDictionaryService.getModel());
        addVehicleDropDownListDto.setTransmissions(defaultDictionaryService.getTransmission());
        addVehicleDropDownListDto.setVehicleCategories(defaultDictionaryService.getVehicleCategory());
        addVehicleDropDownListDto.setVehicleTypes(defaultDictionaryService.getVehicleType());

        return addVehicleDropDownListDto;
    }
}
