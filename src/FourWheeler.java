// FourWheeler.java
public class FourWheeler extends Vehicle {

    public FourWheeler(String vehicleNumber, String ownerName) {
        super(vehicleNumber, ownerName, "FOUR_WHEELER");
    }

    @Override
    public double getBaseRate() { return 20.0; }  // ₹20 for first hour

    @Override
    public double getExtraRatePerHour() { return 10.0; } // ₹10 each extra hour
}