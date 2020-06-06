package GeneticAlgorithm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Yosua Yuuta Bima P
 */
public class Genetic {

    public List<String[]> datas_pengawas_random = new ArrayList<>();
    public List<List<String[]>> childs = new ArrayList<>();
    public List<List<String[]>> error_msg = new ArrayList();
    public List<double[]> fitnesses = new ArrayList<>();
    public List<double[]> chance = new ArrayList();
    public List<String[]> selected_genes = new ArrayList<>();

    public int jml_populasi;
    public int jml_generasi;
    public int crossover_type;
    public double best_fitness;
    public int best_generation;
    public int best_gene;
    //public ;
    public String[] defaultJatahDosen;
    private Generator gr;

    public Genetic(Generator gr) {
        this.gr = gr;
        this.error_msg.add(new ArrayList<>());
    }

    public void addJatah(String[] jatahInput) {
        String[] jatah = new String[gr.datas_dosen.size()];
        defaultJatahDosen = new String[jatah.length];
        Set<String> set_dosen = gr.datas_dosen.keySet();
        String[] dosen = set_dosen.toArray(new String[set_dosen.size()]);
        String[] jabatan = new String[gr.datas_dosen.size()];
        for (int i = 0; i < dosen.length; i++) {
            jabatan[i] = gr.datas_dosen.get(dosen[i])[0];
            if (jabatan[i].equalsIgnoreCase("0")) {
                jatah[i] = jatahInput[0];
            }
            if (jabatan[i].equalsIgnoreCase("1")) {
                jatah[i] = jatahInput[1];
            }
            if (jabatan[i].equalsIgnoreCase("2")) {
                jatah[i] = jatahInput[2];
            }
            if (jabatan[i].equalsIgnoreCase("3")) {
                jatah[i] = jatahInput[3];
            }
            gr.datas_dosen.get(dosen[i])[2] = jatah[i];
            defaultJatahDosen[i] = jatah[i];
        }
    }

    public void resetJatah() {
        String[] jatah = new String[gr.datas_dosen.size()];
        Set<String> set_dosen = gr.datas_dosen.keySet();
        String[] dosen = set_dosen.toArray(new String[set_dosen.size()]);
        String[] jabatan = new String[gr.datas_dosen.size()];
        for (int i = 0; i < dosen.length; i++) {
            gr.datas_dosen.get(dosen[i])[2] = defaultJatahDosen[i];
        }
    }

    public void countFitness() {
        double[] temp_fitness = new double[datas_pengawas_random.size()];
        List<String[]> temp_error_msg = new ArrayList();
        String[] message = new String[childs.get(0).get(0).length];
        //error_msg.add(temp_error_msg);
        //System.out.println("Nilai fitness: ");
        for (int h = 0; h < childs.get(0).size(); h++) {
            resetJatah();
            String[] data_pengawas = childs.get(childs.size() - 1).get(h);
            for (int j = 0; j < data_pengawas.length; j++) {
                message[j] = "";
                List<String> curr_jadwal = gr.datas.get(j);
                if (j > 0) {
                    //Penalty untuk jadwal mengawas yang bentrok
                    for (int k = j - 1; k >= 0; k--) {
                        List<String> before_jadwal = gr.datas.get(k);
                        String before_date = before_jadwal.get(0);
                        String before_start = before_jadwal.get(1);
                        String curr_date = curr_jadwal.get(0);
                        String curr_start = curr_jadwal.get(1);
                        String curr_dosent = data_pengawas[j];
                        String before_dosent = data_pengawas[k];
                        if (curr_date.equalsIgnoreCase(before_date)) {
                            if (curr_start.equalsIgnoreCase(before_start)) {
                                if (curr_dosent.equalsIgnoreCase(before_dosent)) {
                                    temp_fitness[h] = temp_fitness[h] + 0.055;
                                    message[j] += "Bentrok, ";
                                }
                            }
                        } else {
                            break;
                        }
                    }
                }
                //Penalty untuk mata kuliah yang tidak diawasi dosen dengan jurusan yang sama 
                String curr_jadwal_jurusan = gr.datas.get(j).get(4);
                String curr_jadwal_ruangan = gr.datas.get(j).get(5);
                String curr_dosen = datas_pengawas_random.get(h)[j];
                String curr_dosen_jurusan = gr.datas_dosen.get(curr_dosen)[1];
                String curr_dosen_is_programming = gr.datas_dosen.get(curr_dosen)[3];
                if (curr_jadwal_ruangan.equalsIgnoreCase("LAB") && curr_dosen_is_programming.equalsIgnoreCase("0")) {
                    temp_fitness[h] = temp_fitness[h] + 0.005;
                    message[j] += "Bukan dosen programming, ";
                } else {
                    temp_fitness[h] = temp_fitness[h];
                }
                //Penalty untuk dosen yang mengawas lebih dari jatahnya
                int curr_jatah_dosen = Integer.parseInt(gr.datas_dosen.get(curr_dosen)[2]);
                if (curr_jatah_dosen < 0) {
                    temp_fitness[h] = temp_fitness[h] + 0.001;
                    message[j] += "Lebih dari jatah, ";
                } else {
                    curr_jatah_dosen -= 1;
                    String[] new_value = {gr.datas_dosen.get(curr_dosen)[0], gr.datas_dosen.get(curr_dosen)[1],
                        Integer.toString(curr_jatah_dosen), gr.datas_dosen.get(curr_dosen)[3]};
                    gr.datas_dosen.replace(curr_dosen, new_value);
                    gr.datas_dosen.get(curr_dosen)[2] = Integer.toString(curr_jatah_dosen);
                    temp_fitness[h] = temp_fitness[h];
                }

            }
            temp_fitness[h] = 1.0 / (1.0 + temp_fitness[h]);
            System.out.println(temp_fitness[h] + " ");
            temp_error_msg.add(message);
        }
        System.out.println("==================================================");
        fitnesses.add(temp_fitness);
        error_msg.add(temp_error_msg);
    }

    public void findBestFitness() {
        //Mencari fitness terbaik dan posisinya pada childs
        this.best_fitness = 0;
        for (int i = 0; i < fitnesses.size(); i++) {
            for (int j = 0; j < fitnesses.get(i).length; j++) {
                if (fitnesses.get(i)[j] >= best_fitness) {
                    best_fitness = fitnesses.get(i)[j];
                    best_generation = i;
                    best_gene = j;
                }
            }
        }
    }

    public void initialization(int n_population) {
        Random r = new Random();
        Set<String> set_datas_key = gr.datas_dosen.keySet();
        String[] dosen_names = set_datas_key.toArray(new String[set_datas_key.size()]);
        for (int i = 0; i < n_population; i++) {
            String[] temp_dosen = new String[gr.n_jadwal];
            for (int j = 0; j < gr.n_jadwal; j++) {
                temp_dosen[j] = dosen_names[r.nextInt(dosen_names.length)];
            }
            datas_pengawas_random.add(temp_dosen);
        }

        childs.add(datas_pengawas_random);
    }

    public void controlledInitialization(int n_population) {
        Random r = new Random();
        Set<String> set_datas_key = gr.datas_dosen.keySet();
        String[] dosen_names = set_datas_key.toArray(new String[set_datas_key.size()]);
        int totalJatah = 0;
        for (int i = 0; i < dosen_names.length; i++) {
            String curr_dosen = dosen_names[i];
            totalJatah += Integer.parseInt(gr.datas_dosen.get(curr_dosen)[2]);
        }
        String[] dosen_modified = new String[gr.n_jadwal];
        int counter = 0;
        for (int j = 0; j < dosen_names.length; j++) {
            for (int h = 0; h < Integer.parseInt(gr.datas_dosen.get(dosen_names[j])[2]); h++) {
                dosen_modified[counter] = dosen_names[j];
                counter++;
            }
        }
        for (int g = counter; g < gr.n_jadwal; g++) {
            dosen_modified[g] = dosen_names[r.nextInt(dosen_names.length - 1)];
        }
        for (int l = 0; l < n_population; l++) {
            String[] temp_dosen = new String[gr.n_jadwal];
            for (int k = 0; k < gr.n_jadwal; k++) {
                temp_dosen[k] = dosen_modified[r.nextInt(dosen_modified.length)];
            }
            datas_pengawas_random.add(temp_dosen);
        }
        childs.add(datas_pengawas_random);
    }

    public void doCrossover(int crossover_type, List<String[]> parents) {
        List<String[]> temp_childs = new ArrayList<>();
        Random rand = new Random();
        //1-point Crossover 
        if (crossover_type == 1) {
            for (int i = 0; i < parents.size(); i++) {
                String[] parent_1 = parents.get(i);
                String[] parent_2;
                if (i + 1 == parents.size()) {
                    parent_2 = parents.get(0);
                } else {
                    parent_2 = parents.get(i + 1);
                }
                String[] child_1 = new String[parent_1.length];
                String[] child_2 = new String[parent_1.length];
                int divider_point;
                while (true) {
                    divider_point = rand.nextInt(parent_1.length);
                    if (divider_point > 1) {
                        break;
                    }
                }
                divider_point -= 1;
                for (int j = 0; j < parent_1.length; j++) {
                    if (divider_point > j) {
                        child_1[j] = parent_1[j];
                    } else {
                        child_1[j] = parent_2[j];
                    }
                }
                for (int j = 0; j < parent_2.length; j++) {
                    if (divider_point > j) {
                        child_2[j] = parent_2[j];
                    } else {
                        child_2[j] = parent_1[j];
                    }
                }
                temp_childs.add(child_1);
                if (i + 1 != parents.size()) {
                    temp_childs.add(child_2);
                }
                i += 1;
            }
            childs.add(temp_childs);

            //2-point Crossover    
        } else if (crossover_type == 2) {
            for (int i = 0; i < parents.size(); i++) {
                String[] parent_1 = parents.get(i);
                String[] parent_2;
                if (i + 1 == parents.size()) {
                    parent_2 = parents.get(0);
                } else {
                    parent_2 = parents.get(i + 1);
                }
                String[] child_1 = new String[parent_1.length];
                String[] child_2 = new String[parent_1.length];
                int divider_point_1;
                int divider_point_2;
                while (true) {
                    divider_point_1 = rand.nextInt(parent_1.length);
                    divider_point_2 = rand.nextInt(parent_1.length);
                    if (divider_point_1 > 1 && divider_point_2 > 1) {
                        if (divider_point_1 < divider_point_2) {
                            break;
                        }
                    }
                }
                divider_point_1 -= 1;
                divider_point_2 -= 1;
                for (int j = 0; j < parent_1.length; j++) {
                    if (divider_point_1 > j && divider_point_1 < j) {
                        child_1[j] = parent_1[j];
                    } else {
                        child_1[j] = parent_2[j];
                    }
                }
                for (int j = 0; j < parent_2.length; j++) {
                    if (divider_point_1 > j && divider_point_1 < j) {
                        child_2[j] = parent_2[j];
                    } else {
                        child_2[j] = parent_1[j];
                    }
                }
                temp_childs.add(child_1);
                if (i + 1 != parents.size()) {
                    temp_childs.add(child_2);
                }
                i += 1;
            }
            childs.add(temp_childs);

            //Uniform Crossover    
        } else {
            int dividerPoint = 0;
            for (int i = 0; i < parents.size(); i++) {
                String[] parent_1 = parents.get(i);
                String[] parent_2;
                if (i + 1 == parents.size()) {
                    parent_2 = parents.get(0);
                } else {
                    parent_2 = parents.get(i + 1);
                }
                String[] child_1 = new String[parent_1.length];
                String[] child_2 = new String[parent_1.length];
                int counter = 1 + rand.nextInt(1);

                boolean isEven = false;

                for (int j = 0; j < parent_1.length; j++) {
                    if (counter == 0) {
                        counter = 1 + rand.nextInt(1);
                        if (isEven == true) {
                            isEven = false;
                        } else {
                            isEven = true;
                        }
                    }

                    if (counter != 0 && isEven == false) {
                        child_1[j] = parent_2[j];
                    } else {
                        child_1[j] = parent_1[j];
                    }
                    counter--;
                }
                counter = 1 + rand.nextInt(1);
                isEven = false;
                for (int j = 0; j < parent_2.length; j++) {
                    if (counter == 0) {
                        counter = 1 + rand.nextInt(1);
                        if (isEven == true) {
                            isEven = false;
                        } else {
                            isEven = true;
                        }
                    }
                    if (counter != 0 && isEven == false) {
                        child_2[j] = parent_1[j];
                    } else {
                        child_2[j] = parent_2[j];
                    }
                    counter--;
                }
                temp_childs.add(child_1);
                if (i + 1 != parents.size()) {
                    temp_childs.add(child_2);
                }
                i += 1;
            }
            childs.add(temp_childs);
        }
    }

    public void doMutation(int[] point) {
        List<String[]> parents = childs.get(childs.size() - 1);
        List<String[]> temp_childs = new ArrayList<>();
        for (int i = 0; i < parents.size(); i++) {
            String[] parent_1 = parents.get(i);
            if (i + 1 != parents.size()) {
                String[] parent_2 = parents.get(i + 1);
                for (int j = 0; j < point.length; j++) {
                    String temp_point_val = parent_1[point[j]];
                    parent_1[point[j]] = parent_2[point[j]];
                    parent_2[point[j]] = parent_1[point[j]];
                }
                temp_childs.add(parent_1);
                temp_childs.add(parent_2);
            } else {
                temp_childs.add(parent_1);
            }
            i += 1;
        }
    }

    public void doRegenerate() {
        childs.add(childs.get(childs.size() - 1));
    }

    public void countAvgFitness() {
        double[] avg_fitness = new double[fitnesses.size()];
        for (int i = 0; i < fitnesses.size(); i++) {
            double total = 0;
            for (int j = 0; j < fitnesses.get(i).length; j++) {
                total += fitnesses.get(i)[j];
            }
            avg_fitness[i] = total / fitnesses.get(i).length;
            System.out.println("Rata-rata fitness generasi " + (i + 1) + " adalah: " + avg_fitness[i]);
        }
    }

    public void printAllGen() {
        int g = 0;
        for (Iterator<List<String[]>> iterator = childs.iterator(); iterator.hasNext();) {
            List<String[]> next = iterator.next();
            if (g == 0) {
                System.out.println("-------------------- PARENT ------------------------ ");
            } else {
                System.out.println("-------------------- GENERASI KE-" + g + " ------------------------ ");
            }
            for (int i = 0; i < next.size(); i++) {
                System.out.print((i + 1) + ". ");
                for (int j = 0; j < next.get(i).length; j++) {
                    System.out.print(next.get(i)[j] + " ");
                }
                System.out.println("");
            }
            System.out.println("");
            g++;
        }

    }

    public void selection() {
        double[] fitnessTotal = new double[fitnesses.get(0).length];
        double total = 0;
        double[] fitness = fitnesses.get(fitnesses.size() - 1);
        for (int i = 0; i < fitness.length; i++) {
            fitnessTotal[i] = fitness[i];
            total += fitness[i];
        }
        for (int i = 0; i < fitness.length; i++) {
            fitnessTotal[i] = fitnessTotal[i] / total;
        }
        for (int i = 1; i < fitness.length; i++) {
            fitnessTotal[i] += fitnessTotal[i - 1];
        }
        int[] temp_child = new int[fitnessTotal.length];
        Random rand = new Random();
        for (int i = 0; i < fitness.length; i++) {
            double rand_val = rand.nextDouble();
            for (int j = 0; j < fitnessTotal.length; j++) {
                if (rand_val < fitnessTotal[j]) {
                    temp_child[i] = j;
                    break;
                }
            }
        }
        List<String[]> old_parent = childs.get(childs.size() - 1);
        for (int i = 0; i < temp_child.length; i++) {
            selected_genes.add(old_parent.get(temp_child[i]));
        }
    }

    public String printDosenCounter() {

        Set<String> set_dosen = gr.datas_dosen.keySet();
        String[] dosen = set_dosen.toArray(new String[set_dosen.size()]);
        String result = " ==============  Kemunculan Dosen  =============== " + "\n";

        for (int i = 0; i < dosen.length; i++) {
            String curr_dosen = dosen[i];
            int jatah_counter = 0;
            result += (i + 1) + ". " + curr_dosen + " = ";
            for (int j = 0; j < gr.datas.size(); j++) {
                if (childs.get(best_generation).get(best_gene)[j] == curr_dosen) {
                    jatah_counter++;
                }
            }
            result += Integer.toString(jatah_counter) + "\n";
        }
        return result;
    }

    public String bestResult() {
        System.out.println("errors : " + error_msg.size());
        String result = "";
        result += " =========  HASIL PENJADWALAN  ========= " + "\n";
        result += "No; Mulai; Selesai; Mata Kuliah; Jurusan; Ruangan; Pengawas ;  " + "\n";
        System.out.println(" =========  HASIL PENJADWALAN  ========= ");
        for (int i = 0; i < gr.datas.size(); i++) {
            System.out.print((i + 1) + ". ");
            result += (i + 1) + ". ";
            List<String> curr_jadwal = gr.datas.get(i);
            for (int j = 0; j < curr_jadwal.size() - 1; j++) {
                result += curr_jadwal.get(j) + " ";
                System.out.print(curr_jadwal.get(j) + " ");
            }
            result += childs.get(best_generation).get(best_gene)[i] + "\n";
            System.out.println(childs.get(best_generation).get(best_gene)[i] + " ");
//            result += error_msg.get(best_generation).get(best_gene)[i] + "\n";
//            System.out.println(error_msg.get(best_generation).get(best_gene)[i] + " ");
        }
        System.out.println("Jumlah Generasi : " + childs.size());
        return result;
    }

}
