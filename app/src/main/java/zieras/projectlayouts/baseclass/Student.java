package zieras.projectlayouts.baseclass;

/**
 * Created by zieras on 21/12/2015.
 */
public class Student {
    private String name, matricNo, macAddress;

    public Student(String name, String matricNo, String macAddress) {
        this.name = name;
        this.matricNo = matricNo;
        this.macAddress = macAddress;
    }

    public void setName(String name) { this.name = name; }
    public void setMatricNo(String matricNo) { this.matricNo = matricNo; }
    public void setMacAddress(String macAddress) { this.macAddress = macAddress; }

    public String getName() { return name; }
    public String getMatricNo() {
        return matricNo;
    }
    public String getMacAddress() { return macAddress; }

    public String toString() {
        return "Name: " + name + " " + "Matric: " +  matricNo + " " + "Mac: " + macAddress;
    }
}
