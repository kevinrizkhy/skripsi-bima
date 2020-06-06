package GeneticAlgorithm;

import GUI.UserInterface;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JTextArea;

/**
 *
 * @author Yosua Yuuta Bima Pramana
 */
public class tester {

    public static Generator gr = new Generator();
    public static Genetic gen = new Genetic(gr);

    public void mainGUI(
            String jml_populasi, String jml_generasi, String jadwal_path, String dosen_path,
            String jatah_dekan, String jatah_prodi, String jatah_tetap, String jatah_kontrak,
            int crossover_type, float cross_rate, float mut_rate,
            JTextArea view_hasil, JTextArea view_dosen, JLabel time_elapse, JLabel best_fitness) {

        long startTime = System.currentTimeMillis();
        Random parameter = new Random();
        String[] jatahInput = new String[4];
        if (jml_generasi.isEmpty() || jml_populasi.isEmpty()) {
            view_hasil.setText("Lengkapi Data Terlebih Dahulu");
        } else {

            try {
                gr.insertJadwal(jadwal_path);
                gr.insertDosen(dosen_path);

            } catch (IOException ex) {
                Logger.getLogger(UserInterface.class.getName()).log(Level.SEVERE, null, ex);
            }
            for (int i = 0; i < jatahInput.length; i++) {
                switch (i) {
                    case 0:
                        jatahInput[i] = jatah_dekan;
                        break;
                    case 1:
                        jatahInput[i] = jatah_prodi;
                        break;
                    case 2:
                        jatahInput[i] = jatah_tetap;
                        break;
                    case 3:
                        jatahInput[i] = jatah_kontrak;
                        break;
                    default:
                        break;
                }
            }
            gen.crossover_type = crossover_type + 1;
            gen.jml_generasi = Integer.parseInt(jml_generasi);
            gen.jml_populasi = Integer.parseInt(jml_populasi);

            //INPUT NILAI-NILAI ALGORITMA
            float crossRate_input = cross_rate;
            float mutRate_input = mut_rate;

            //RANDOM PARAMETER CROSSOVER DAN MUTASI
            float crossRate = parameter.nextFloat();
            //System.out.println(crossRate);
            float mutRate = parameter.nextFloat();
            //System.out.println(mutRate);
            int[] mutPoint = new int[gr.n_jadwal / 10];
            for (int i = 0; i < mutPoint.length; i++) {
                mutPoint[i] = parameter.nextInt(gr.n_jadwal);
            }
            gen.addJatah(jatahInput);
            gen.controlledInitialization(gen.jml_populasi);
            for (int i = 0; i < gen.jml_generasi; i++) {
                gen.selected_genes = new ArrayList<>();
                System.out.println("GENERASI KE-" + i);
                gen.countFitness();
                gen.findBestFitness();
                gen.addJatah(jatahInput);
                gen.selection();
                if (crossRate > crossRate_input) {
                    gen.doCrossover(gen.crossover_type, gen.selected_genes);
                } else {
                    gen.doRegenerate();
                    System.out.println("Tidak terjadi Crossover");
                }
                if (mutRate > mutRate_input) {
                    gen.doMutation(mutPoint);
                } else {
                    System.out.println("Tidak terjadi Mutasi");
                }
                System.out.println("");
                crossRate = parameter.nextFloat();
                mutRate = parameter.nextFloat();
            }//           
            long endTime = System.currentTimeMillis();
            long processTime = (endTime - startTime) / 1000;

            time_elapse.setText(Long.toString(processTime));
            best_fitness.setText(Double.toString(gen.best_fitness));
            view_hasil.setText(gen.bestResult());
            view_dosen.setText(gen.printDosenCounter());
        }
    }

        // 10 2 1 1 2 3 2 0 1
    @SuppressWarnings("empty-statement")
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        String jadwal_path = "files\\FileJadwalPengawas.csv";
        String dosen_path = "files\\dosentest.csv";
        System.out.print("Jumlah populasi : ");
        String jml_populasi = sc.next();
        System.out.print("Jumlah generasi : ");
        String jml_generasi = sc.next();
        System.out.print("Jumlah jatah dekan : ");
        String jatah_dekan = sc.next();
        System.out.print("Jumlah jatah prodi : ");
        String jatah_prodi = sc.next();
        System.out.print("Jumlah jatah tetap : ");
        String jatah_tetap = sc.next();
        System.out.print("Jumlah jatah kontrak : ");
        String jatah_kontrak = sc.next();
        System.out.print("Jumlah crossover type : ");
        int crossover_type = sc.nextInt();
        System.out.print("Jumlah cross rate : ");
        float cross_rate = sc.nextFloat();
        System.out.print("Jumlah mutation rate : ");
        float mut_rate = sc.nextFloat();

        long startTime = System.currentTimeMillis();
        Random parameter = new Random();
        if (jml_generasi.isEmpty() || jml_populasi.isEmpty()) {
            System.out.println("Lengkapi Data Terlebih Dahulu");
        } else {
            Generator generator = new Generator();
            Genetic genetic = new Genetic(generator);
            try {
                generator.insertJadwal(jadwal_path);
                generator.insertDosen(dosen_path);

            } catch (IOException ex) {
                Logger.getLogger(UserInterface.class.getName()).log(Level.SEVERE, null, ex);
            }

            String jatahInput[] = new String[]{jatah_dekan, jatah_prodi, jatah_tetap, jatah_kontrak};

            genetic.crossover_type = crossover_type + 1;
            genetic.jml_generasi = Integer.parseInt(jml_generasi);
            genetic.jml_populasi = Integer.parseInt(jml_populasi);

            //INPUT NILAI-NILAI ALGORITMA
            float crossRate_input = cross_rate;
            float mutRate_input = mut_rate;

            //RANDOM PARAMETER CROSSOVER DAN MUTASI
            float crossRate = parameter.nextFloat();
            float mutRate = parameter.nextFloat();
            int[] mutPoint = new int[generator.n_jadwal / 10];
            for (int i = 0; i < mutPoint.length; i++) {
                mutPoint[i] = parameter.nextInt(generator.n_jadwal);
            }
            genetic.addJatah(jatahInput);
            genetic.initialization(genetic.jml_populasi);
            for (int i = 0; i < genetic.jml_generasi; i++) {
                genetic.selected_genes = new ArrayList<>();
                genetic.countFitness();
                genetic.findBestFitness();
                genetic.selection();
                if (crossRate > crossRate_input) {
                    genetic.doCrossover(genetic.crossover_type, genetic.selected_genes);
                } else {
                    genetic.doRegenerate();
                    System.out.println("Tidak terjadi Crossover");
                }
                if (mutRate < mutRate_input) {
                    genetic.doMutation(mutPoint);
                } else {
                    System.out.println("Tidak terjadi Mutasi");
                }
                System.out.println("");
                crossRate = parameter.nextFloat();
                mutRate = parameter.nextFloat();
                System.out.println("--------------------------------------------------------------");
            }
            long endTime = System.currentTimeMillis();
            long processTime = (endTime - startTime) / 1000;

            //genetic.printAllGen();
            genetic.bestResult();
            System.out.println("EXECUTION TIME : " + Long.toString(processTime) + "s");
            System.out.println("BEST : " + Double.toString(genetic.best_fitness));
            toCSV(genetic, generator);
        }
    }


    public static void toCSV(Genetic genetic, Generator generator) {
        try (PrintWriter writer = new PrintWriter(new File("hasil_jadwal.csv"))) {
            StringBuilder sb = new StringBuilder();
            sb.append("Tanggal");
            sb.append(';');
            sb.append("Mulai");
            sb.append(';');
            sb.append("Selesai");
            sb.append(';');
            sb.append("Mata Kuliah");
            sb.append(';');
            sb.append("Jurusan");
            sb.append(';');
            sb.append("Ruang");
            sb.append(';');
            sb.append("Pengawas");
            sb.append(';');
            sb.append("ERROR");
            sb.append('\n');
            int counter = 0;

            for (int i = 0; i < generator.datas.size(); i++) {
                List<String> curr_jadwal = generator.datas.get(i);
                for (int j = 0; j < curr_jadwal.size() - 1; j++) {
                    sb.append(curr_jadwal.get(j) + ";");
                }
                sb.append(genetic.childs.get(genetic.best_generation).get(genetic.best_gene)[i]).append(";");
                sb.append(genetic.error_msg.get(genetic.best_generation).get(genetic.best_gene)[i]).append("\n");
                if (genetic.error_msg.get(genetic.best_generation).get(genetic.best_gene)[i].isEmpty()) {
                    counter++;
                }
            }
            sb.append("\nBEST : " + Double.toString(gen.best_fitness));
            sb.append('\n');
            sb.append("Total kesalahan: " + (gr.datas.size() - counter));
            sb.append('\n');
            writer.write(sb.toString());
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
