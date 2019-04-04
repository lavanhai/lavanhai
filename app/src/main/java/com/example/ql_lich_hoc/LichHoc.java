package com.example.ql_lich_hoc;

public class LichHoc {
    private String Mon, Phong, ThoiGian;
    private int ID;

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

    public LichHoc(String mon, String phong, String thoiGian, int ID) {
        Mon = mon;
        Phong = phong;
        ThoiGian = thoiGian;
        this.ID = ID;
    }
}
