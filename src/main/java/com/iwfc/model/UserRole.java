package com.iwfc.model;

/**
 * Enum: UserRole
 * 
 * Purpose:
 * Defines the 3 primary actors specified in the assessment requirements:
 * 1. ADMIN: Manages equipment inventory, oversees user accounts, and monitors global maintenance.
 * 2. INSTRUCTOR: Schedules fitness sessions and reports equipment faults.
 * 3. MEMBER: Views schedules, books class slots, and receives notifications.
 * 
 * OOP Concept:
 * - Enums guarantee type safety and prevent illegal role values.
 */
public enum UserRole {
    ADMIN,
    INSTRUCTOR,
    MEMBER
}