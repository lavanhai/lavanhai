package com.example.ql_lich_hoc;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    int codeLichHoc, codeLichThi;
    AlarmManager alarmManager;
    Calendar calendar = Calendar.getInstance();
    int Gio, Phut, Nam, Thang, Ngay;
    TextView txtvChonGio, txtvChonNgay;
    Database database;
    FloatingActionButton fab;
    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        anhxa();
        database = new Database(MainActivity.this, "QL_LichHoc.sqlite", null, 1);
        database.exucute("CREATE TABLE IF NOT EXISTS LichHoc(Id INTEGER PRIMARY KEY AUTOINCREMENT,Mon VARCHAR(50),Phong VARCHAR(10),ThoiGian VARCHAR(100))");
        database.exucute("CREATE TABLE IF NOT EXISTS LichThi(Id INTEGER PRIMARY KEY AUTOINCREMENT,Mon VARCHAR(50),Phong VARCHAR(10),Sbd VARCHAR(10),ThoiGian VARCHAR(100))");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogAdd();
            }
        });
        SetupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void DialogAdd() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_add);
        dialog.show();
        txtvChonGio = (TextView) dialog.findViewById(R.id.ChonGio);
        txtvChonNgay = (TextView) dialog.findViewById(R.id.ChonNgay);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        txtvChonGio.setText(simpleDateFormat.format(calendar.getTime()));
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
        txtvChonNgay.setText(simpleDateFormat1.format(calendar.getTime()));
        txtvChonNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        calendar.set(year, month, dayOfMonth);
                        txtvChonNgay.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                }, Nam, Thang, Ngay);
                datePickerDialog.show();
            }
        });

        txtvChonGio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                        calendar.set(Nam, Thang, Ngay, hourOfDay, minute);
                        txtvChonGio.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                }, Gio, Phut, true);
                timePickerDialog.show();
            }
        });

        dialog.findViewById(R.id.buttonHuy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.buttonThem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor dataLichHoc = database.getData("SELECT * FROM LichHoc");
                while (dataLichHoc.moveToNext()) {
                    codeLichHoc = dataLichHoc.getInt(0);
                }
                Cursor dataLichThi = database.getData("SELECT * FROM LichThi");
                while (dataLichThi.moveToNext()) {
                    codeLichThi = dataLichThi.getInt(0);
                }
                EditText edtMon = (EditText) dialog.findViewById(R.id.editTextMon);
                EditText edtphong = (EditText) dialog.findViewById(R.id.editTextPhong);
                EditText edtSBD = (EditText) dialog.findViewById(R.id.editTextSBD);
                RadioButton rdLichHoc = (RadioButton) dialog.findViewById(R.id.radioButtonLichHoc);
                RadioButton rdLichThi = (RadioButton) dialog.findViewById(R.id.radioButtonLichThi);
                String thoigian = txtvChonGio.getText().toString() + " Ngày " + txtvChonNgay.getText().toString();

                if (rdLichHoc.isChecked()) {
                    if(edtMon.getText().toString().length()==0 || edtphong.getText().toString().length()==0){
                        Toast.makeText(MainActivity.this,"Vui lòng nhập đầy đủ thông tin!",Toast.LENGTH_SHORT).show();
                    }else {
                        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                        PendingIntent pendingIntentLichHoc = PendingIntent.getBroadcast(MainActivity.this, codeLichHoc, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        ;
                        BaoDong(pendingIntentLichHoc, codeLichHoc);
                        database.exucute("INSERT INTO LichHoc VALUES(null,'" + edtMon.getText().toString() + "','" + edtphong.getText().toString() + "','" + thoigian + "')");
                        Toast.makeText(MainActivity.this,"Thêm lịch học thành công!",Toast.LENGTH_SHORT).show();
                        if (fragmentLicHoc != null) fragmentLicHoc.reloadLichHoc();
                        dialog.dismiss();
                    }
                } else if (rdLichThi.isChecked()) {
                    if (edtMon.getText().toString().length() != 0 && edtphong.getText().toString().length() != 0 && edtSBD.getText().toString().length() != 0){
                        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                        PendingIntent pendingIntentLichThi = PendingIntent.getBroadcast(MainActivity.this, codeLichThi, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        ;
                        BaoDong(pendingIntentLichThi, codeLichThi);
                        database.exucute("INSERT INTO LichThi VALUES(null,'" + edtMon.getText().toString() + "','" + edtphong.getText().toString() + "','" + edtSBD.getText().toString() + "','" + thoigian + "')");
                        Toast.makeText(MainActivity.this,"Thêm lịch thi thành công!",Toast.LENGTH_SHORT).show();
                        if (fragmentLichThi != null) fragmentLichThi.reloadLichThi();
                        dialog.dismiss();
                    }else {
                        Toast.makeText(MainActivity.this,"Vui lòng nhập đầy đủ thông tin!",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }

    private void BaoDong(PendingIntent pendingIntent, int code) {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private FragmentLichHoc fragmentLicHoc;
    private FragmentLichThi fragmentLichThi;

    private void SetupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(fragmentLicHoc = new FragmentLichHoc(), "Lịch Học");
        adapter.AddFragment(fragmentLichThi = new FragmentLichThi(), "Lịch Thi");
        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        List<Fragment> fragmentList = new ArrayList<>();
        List<String> titlelist = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return fragmentList.get(i);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void AddFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            titlelist.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titlelist.get(position);
        }
    }

    private void anhxa() {
        fab = findViewById(R.id.fab);
        viewPager = findViewById(R.id.ViewPager1);
        tabLayout = findViewById(R.id.Tablayout1);
        Gio = calendar.get(Calendar.HOUR_OF_DAY);
        Phut = calendar.get(Calendar.MINUTE);
        Ngay = calendar.get(Calendar.DAY_OF_MONTH);
        Thang = calendar.get(Calendar.MONTH);
        Nam = calendar.get(Calendar.YEAR);
    }

}
