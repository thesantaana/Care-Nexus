package com.carenexus.doctor.constant;

public final class DoctorConstants {

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_DOCTOR = "DOCTOR";
    public static final String ROLE_HEALTH_MANAGER = "HEALTH_MANAGER";

    public static final String PERMISSION_ELDER_VIEW = "doctor:elder:view";
    public static final String PERMISSION_ELDER_AUTHORIZE = "doctor:elder:authorize";
    public static final String PERMISSION_HEALTH_MANAGE = "doctor:health:manage";

    public static final String ACTIVE = "ACTIVE";
    public static final String CANCELLED = "CANCELLED";
    public static final String DRAFT = "DRAFT";
    public static final String CONFIRMED = "CONFIRMED";
    public static final String PENDING = "PENDING";
    public static final String PROCESSING = "PROCESSING";
    public static final String CLOSED = "CLOSED";
    public static final String SOURCE_THRESHOLD = "THRESHOLD";
    public static final String SOURCE_MANUAL = "MANUAL";
    public static final String LEVEL_WARNING = "WARNING";

    private DoctorConstants() {
    }
}
