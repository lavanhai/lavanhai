package com.example.ql_lich_hoc;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class FragmentLichHoc extends Fragment {
    RecyclerView recyclerView;
    Database database;
    ArrayList<LichHoc> lichHocArrayList = new ArrayList<>();
    AdapterLichHoc adapterLichHoc;
    EditText edtSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lichhoc, container, false);
        database = new Database(getActivity(), "QL_LichHoc.sqlite", null, 1);
        database.exucute("CREATE TABLE IF NOT EXISTS LichHoc(Id INTEGER PRIMARY KEY AUTOINCREMENT,Mon VARCHAR(50),Phong VARCHAR(10),ThoiGian VARCHAR(100))");
        initView(view);
        edtSearch = (EditText)view.findViewById(R.id.editTextSearch);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                SeachLichHoc(s.toString());
            }
        });
        return view;
    }
    private void SeachLichHoc(String text){
        ArrayList<LichHoc> lichHocs = new ArrayList<>();
        for (LichHoc item :lichHocArrayList){
            if(item.getMon().toLowerCase().contains(text.toLowerCase())){
                lichHocs.add(item);
            }
        }
        adapterLichHoc.SeachListLichHoc(lichHocs);
        adapterLichHoc.notifyDataSetChanged();
    }

    private void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterLichHoc = new AdapterLichHoc(lichHocArrayList, getActivity());
        adapterLichHoc.setOnChangeData(new AdapterLichHoc.OnChangeData() {
            @Override
            public void onChange() {
                reloadLichHoc();
            }
        });
        recyclerView.setAdapter(adapterLichHoc);
        reloadLichHoc();
    }

    public void reloadLichHoc() {
        if (isAdded()) {
            lichHocArrayList.clear();
            Cursor data = database.getData("SELECT * FROM LichHoc");
            while (data.moveToNext()) {
                int ID = data.getInt(0);
                String Mon = data.getString(1);
                String Phong = data.getString(2);
                String ThoiGian = data.getString(3);
                lichHocArrayList.add(new LichHoc(Mon, Phong, ThoiGian, ID));
                adapterLichHoc.notifyDataSetChanged();
            }
        }
    }
}

class AdapterLichHoc extends RecyclerView.Adapter<AdapterLichHoc.ViewHolder> {
    ArrayList<LichHoc> lichHocArrayList;
    Context context;
    OnChangeData onChangeData;

    public void setOnChangeData(OnChangeData onChangeData) {
        this.onChangeData = onChangeData;
    }

    public interface OnChangeData {
        void onChange();
    }

    public AdapterLichHoc(ArrayList<LichHoc> lichHocArrayList, Context context) {
        this.lichHocArrayList = lichHocArrayList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.item_lichhoc, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.txtvMon.setText("Môn: " + lichHocArrayList.get(i).getMon());
        viewHolder.txtvPhong.setText("Phòng: " + lichHocArrayList.get(i).getPhong());
        viewHolder.txtvTG.setText("Thời Gian: " + lichHocArrayList.get(i).getThoiGian());

    }

    @Override
    public int getItemCount() {
        return lichHocArrayList.size();
    }
    public void SeachListLichHoc(ArrayList<LichHoc> lichHocs){
        lichHocArrayList = lichHocs;
        notifyDataSetChanged();
    }

    public void removeItem(int posion) {
        lichHocArrayList.remove(posion);
        notifyItemRemoved(posion);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        AlarmManager alarmManager;
        Calendar calendar = Calendar.getInstance();
        int Gio, Phut, Nam, Thang, Ngay;
        TextView txtvChonGio, txtvChonNgay, txtvTime;
        int codeLichHoc;
        Database database = new Database(context, "QL_LichHoc.sqlite", null, 1);
        TextView txtvMon, txtvPhong, txtvTG;
        EditText edtMon, edtphong;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Gio = calendar.get(Calendar.HOUR_OF_DAY);
            Phut = calendar.get(Calendar.MINUTE);
            Ngay = calendar.get(Calendar.DAY_OF_MONTH);
            Thang = calendar.get(Calendar.MONTH);
            Nam = calendar.get(Calendar.YEAR);
            database.exucute("CREATE TABLE IF NOT EXISTS LichHoc(Id INTEGER PRIMARY KEY AUTOINCREMENT,Mon VARCHAR(50),Phong VARCHAR(10),ThoiGian VARCHAR(100))");
            txtvMon = (TextView) itemView.findViewById(R.id.textViewMon);
            txtvPhong = (TextView) itemView.findViewById(R.id.textViewPhong);
            txtvTG = (TextView) itemView.findViewById(R.id.textViewThoiGian);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ID = lichHocArrayList.get(getAdapterPosition()).getID();
                    DialogSua();
                    Cursor dataLichHoc = database.getData("SELECT * FROM LichHoc WHERE Id = '" + ID + "'");
                    while (dataLichHoc.moveToNext()) {
                        String Mon = dataLichHoc.getString(1);
                        String Phong = dataLichHoc.getString(2);
                        String ThoiGian = dataLichHoc.getString(3);
                        edtMon.setText(Mon);
                        edtphong.setText(Phong);
                        txtvTime.setText(ThoiGian);
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int id = lichHocArrayList.get(getAdapterPosition()).getID();
                    database.exucute("DELETE FROM LichHoc WHERE Id = '" + id + "'");
                    HuyBaoDong();
                    removeItem(getAdapterPosition());
                    Toast.makeText(context, "Xóa thành công! " + txtvMon.getText(), Toast.LENGTH_SHORT).show();
                    if (onChangeData != null) onChangeData.onChange();
                    return false;
                }
            });
        }

        private void DialogSua() {
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_edit_lich_hoc);
            dialog.show();
            edtMon = (EditText) dialog.findViewById(R.id.editTextMon);
            edtphong = (EditText) dialog.findViewById(R.id.editTextPhong);
            txtvChonGio = (TextView) dialog.findViewById(R.id.ChonGio);
            txtvChonNgay = (TextView) dialog.findViewById(R.id.ChonNgay);
            txtvTime = (TextView) dialog.findViewById(R.id.textViewTime);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            txtvChonGio.setText(simpleDateFormat.format(calendar.getTime()));
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
            txtvChonNgay.setText(simpleDateFormat1.format(calendar.getTime()));
            txtvChonNgay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            calendar.set(year, month, dayOfMonth);
                            txtvChonNgay.setText(simpleDateFormat.format(calendar.getTime()));
                            txtvTime.setText(txtvChonGio.getText().toString() + " Ngày " + txtvChonNgay.getText().toString());
                        }
                    }, Nam, Thang, Ngay);
                    datePickerDialog.show();
                }
            });

            txtvChonGio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                            calendar.set(Nam, Thang, Ngay, hourOfDay, minute);
                            txtvChonGio.setText(simpleDateFormat.format(calendar.getTime()));
                            txtvTime.setText(txtvChonGio.getText().toString() + " Ngày " + txtvChonNgay.getText().toString());
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
                    int id = lichHocArrayList.get(getAdapterPosition()).getID();
                    String Mon = edtMon.getText().toString();
                    String Phong = edtphong.getText().toString();
                    String Time = txtvTime.getText().toString();
                    if(Mon.length() != 0 && Phong.length() != 0){
                        Intent intent = new Intent(context, AlarmReceiver.class);
                        PendingIntent pendingIntentLichHoc = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        UpdateBaoDong(pendingIntentLichHoc, id);
                        database.exucute("UPDATE LichHoc SET Mon = '" + Mon + "',Phong = '" + Phong + "',ThoiGian = '" + Time + "' WHERE Id = '" + id + "'");
                        Toast.makeText(context, "Sửa thành công!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        if (onChangeData != null) onChangeData.onChange();
                    }else {
                        Toast.makeText(context,"Thông tin không được để trống!",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        private void UpdateBaoDong(PendingIntent pendingIntent, int code) {
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

        private void HuyBaoDong() {
            Cursor dataLichHoc = database.getData("SELECT * FROM LichHoc");
            while (dataLichHoc.moveToNext()) {
                codeLichHoc = dataLichHoc.getInt(0);
            }
            Intent intent = new Intent(context, AlarmReceiver.class);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntentLichHoc = PendingIntent.getBroadcast(context, codeLichHoc, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            ;
            alarmManager.cancel(pendingIntentLichHoc);
        }
    }

}

