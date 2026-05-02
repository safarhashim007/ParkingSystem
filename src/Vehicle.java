public abstract class Vehicle {
    protected String vehicleNumber;
    protected String ownerName;
    protected String vehicleType; // "TWO_WHEELER" or "FOUR_WHEELER"

    public Vehicle(String vehicleNumber, String ownerName, String vehicleType) {
        this.vehicleNumber = vehicleNumber.toUpperCase();
        this.ownerName     = ownerName;
        this.vehicleType   = vehicleType;
    }

    // Abstract method — each subclass defines its hourly rate
    public abstract double getBaseRate();
    public abstract double getExtraRatePerHour();

    public String getVehicleNumber() { return vehicleNumber; }
    public String getOwnerName()     { return ownerName; }
    public String getVehicleType()   { return vehicleType; }

    @Override
    public String toString() {
        return vehicleType + " | " + vehicleNumber + " | Owner: " + ownerName;
    }
}