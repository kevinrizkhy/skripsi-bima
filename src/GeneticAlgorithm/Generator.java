package GeneticAlgorithm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Yosua Yuuta Bima P
 */
public class Generator {

    public static List<List<String>> datas = new ArrayList<>();
    public static HashMap<String, String[]> datas_dosen = new HashMap<String, String[]>();
    public static int n_jadwal;

    public void insertJadwal(String path_jadwal) throws IOException {
        List<String> dataJadwalCSV = Files.readAllLines(Paths.get(path_jadwal));
        dataJadwalCSV.remove(0);
        try {
            for (int i = 0; i < dataJadwalCSV.size(); i++) {
                String[] arr_jadwal = dataJadwalCSV.get(i).split(";");
                //System.out.println(arr_jadwal.length);
                datas.add(Arrays.asList(arr_jadwal));
            }
            n_jadwal = datas.size();
            //System.out.println(n_jadwal);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //System.out.println(datas.size());
    }

    public void insertDosen(String path_dosen) throws IOException {
        List<String> dataListDosen = Files.readAllLines(Paths.get(path_dosen));
        dataListDosen.remove(0);
        for (String line : dataListDosen) {
            String column[] = line.split(";");
            String[] new_value = {column[1], column[2], column[3], column[4]};
            datas_dosen.put(column[0], new_value);
        }
    }
    
    

}
