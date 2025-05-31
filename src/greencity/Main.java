package greencity;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Main {

    /* ───── logging setup (static block) ───── */
    private static final Logger LOG = Logger.getLogger(Main.class.getName());
    static {
        Logger root = Logger.getLogger("");
        root.setLevel(Level.INFO);
        for (var h : root.getHandlers()) root.removeHandler(h);

        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.INFO);
        ch.setFormatter(new java.util.logging.SimpleFormatter() {
            private static final String FMT = "%1$tF %1$tT | %4$-7s | %5$s%6$s%n";
            @Override public synchronized String format(java.util.logging.LogRecord r) {
                return String.format(FMT, r.getMillis(), null, r.getLoggerName(),
                                     r.getLevel(), r.getMessage(), "");
            }
        });
        root.addHandler(ch);
    }

    /* date-stamp for file names */
    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public static void main(String[] args) throws Exception {

        /* 1️⃣  locate CSV */
        Path csv = (args.length > 0)
                   ? Path.of(args[0])
                   : Path.of("dataset", "green_city_devices.csv");

        if (!Files.exists(csv)) {
            LOG.severe("CSV not found: " + csv.toAbsolutePath());
            System.exit(1);
        }
        LOG.info("Loading dataset from: " + csv.toAbsolutePath());
        List<Device> devices = DatasetLoader.load(csv);
        LOG.info("Loaded " + devices.size() + " devices.");

        /* 2️⃣  ensure /outputs exists */
        Path outDir = Path.of("outputs");
        Files.createDirectories(outDir);

        /* 3️⃣  interactive loop */
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.print("\nEnter your available budget in TRY (0 = exit): ₺");
                if (!sc.hasNextDouble()) { sc.nextLine(); continue; }
                double budget = sc.nextDouble();
                sc.nextLine();                       // consume newline

                if (budget <= 0) {
                    LOG.info("Exit requested – terminating.");
                    break;
                }
                LOG.info("Budget entered: ₺" + budget);

                long t0 = System.currentTimeMillis();
                List<Device> chosen = KnapsackSolver.solve(devices, budget);
                LOG.info("Optimisation finished in "
                         + (System.currentTimeMillis() - t0) + " ms");

                String report = buildReport(chosen);
                System.out.print(report);            // echo to console
                writeReport(outDir, chosen, report); // persist to /outputs

                System.out.print("Run another budget? (y/n): ");
                if (!sc.nextLine().trim().equalsIgnoreCase("y")) break;
            }
        }
    }

    /* ───────────── build human-readable report ───────────── */
    private static String buildReport(List<Device> chosen) {
        double totalCost   = chosen.stream().mapToDouble(Device::costTRY).sum();
        double totalEnergy = chosen.stream().mapToDouble(Device::energySavedKWh).sum();
        double sustainSum  = chosen.stream().mapToDouble(Device::objectiveValue).sum();

        StringBuilder sb = new StringBuilder();
        sb.append("\n================ RESULTS ================\n");
        chosen.forEach(d -> sb.append(" • ").append(d).append('\n'));
        sb.append("-----------------------------------------\n");
        sb.append(String.format("Total devices:     %d%n", chosen.size()));
        sb.append(String.format("Total cost:        ₺%.2f%n", totalCost));
        sb.append(String.format("Energy saved:      %.1f kWh / year%n", totalEnergy));
        sb.append(String.format("Σ(energy×score):   %.1f%n", sustainSum));
        sb.append("=========================================\n\n");
        return sb.toString();
    }

    /* ───────────── persist to /outputs folder ───────────── */
    private static void writeReport(Path outDir, List<Device> chosen, String report) {
        double totalCost = chosen.stream().mapToDouble(Device::costTRY).sum();
        long   rounded   = Math.round(totalCost);  // drop decimals for file name

        String ts   = LocalDateTime.now().format(TS_FMT);
        String name = String.format("%dTL_with%ddevice_%s.txt",
                                    rounded, chosen.size(), ts);

        Path outFile = outDir.resolve(name);
        try {
            Files.writeString(outFile, report, StandardCharsets.UTF_8);
            LOG.info("Saved report → " + outFile.toAbsolutePath());
        } catch (Exception e) {
            LOG.severe("Could not write report: " + e.getMessage());
        }
    }

    private Main() {}
}
