package com.example.ql_lich_hoc;

public class LichThi {
    private String Mon, Phong, SBD, ThoiGian;
    int ID;

    public String getMon() {
        return Mon;
    }

    public void setMon(String mon) {
        Mon = mon;
    }

    public String getPhong() {
        return Phong;
    }

    public void setPhong(String phong) {
        Phong = phong;
    }

    public String getSBD() {
        return SBD;
    }

    public void setSBD(String SBD) {
        this.SBD = SBD;
    }

    public String getThoiGian() {
        return ThoiGian;
    }

    public void setThoiGian(String thoiGian) {
        ThoiGian = thoiGian;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public LichThi(String mon, String phong, String SBD, String thoiGian, int ID) {
        Mon = mon;
        Phong = phong;
        this.SBD = SBD;
        ThoiGian = thoiGian;
        this.ID = ID;
    }
}
