package com.iwfc.pattern.creational;

import com.iwfc.model.Equipment;


public class EquipmentFactory {

    /**
     * Factory Method to manufacture Equipment instances with appropriate thresholds.
     *
     * @param type The category of equipment (e.g. "TREADMILL", "SPIN_BIKE", "ROWER").
     * @param id The unique asset ID.
     * @param name The brand/model name.
     * @param location The gym floor room/zone.
     * @return Fully configured Equipment instance.
     */
    public static Equipment createEquipment(String type, String id, String name, String location) {
        // Modern Java Switch Expression to return specialized Equipment objects
        return switch (type.toUpperCase()) {
            case "TREADMILL" -> new Equipment(id, name, "Treadmill", location, 150.0);
            case "SPIN_BIKE"  -> new Equipment(id, name, "Spin Bike", location, 100.0);
            case "ROWER"      -> new Equipment(id, name, "Rowing Machine", location, 120.0);
            default           -> new Equipment(id, name, type, location, 200.0);
        };
    }
}