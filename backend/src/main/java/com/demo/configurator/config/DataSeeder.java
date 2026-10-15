package com.demo.configurator.config;

import com.demo.configurator.model.ConflictRule;
import com.demo.configurator.model.OptionGroup;
import com.demo.configurator.model.Product;
import com.demo.configurator.model.ProductOption;
import com.demo.configurator.model.User;
import com.demo.configurator.repository.ConflictRuleRepository;
import com.demo.configurator.repository.ProductRepository;
import com.demo.configurator.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final ConflictRuleRepository conflictRuleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(ProductRepository productRepository, ConflictRuleRepository conflictRuleRepository,
                     UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.productRepository = productRepository;
        this.conflictRuleRepository = conflictRuleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedAdmin();
        if (productRepository.count() > 0) {
            return;
        }
        seedLaptop();
        seedBike();
        seedCatalog();
    }

    private void seedAdmin() {
        if (userRepository.existsByEmail("admin@example.com")) {
            return;
        }
        User admin = new User();
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("Admin123!"));
        admin.setDisplayName("Administrator");
        admin.setRole("ADMIN");
        userRepository.save(admin);
    }

    private String image(String seed) {
        return "https://picsum.photos/seed/" + seed + "/640/480";
    }

    private void seedLaptop() {
        Product p = new Product();
        p.setName("Custom Laptop Pro");
        p.setSlug("custom-laptop-pro");
        p.setDescription("Configure your ideal laptop: CPU, memory, storage, display, graphics and warranty.");
        p.setCategory("Computing");
        p.setImageUrl(image("laptop-pro"));
        p.setBasePrice(new BigDecimal("899.00"));

        OptionGroup cpu = group(p, "cpu", "Processor", "single", true, 1, 1, 0);
        ProductOption i5 = opt(cpu, "Intel Core i5", "Balanced performance", "CPU-I5", BigDecimal.ZERO, 50, true, true, 0);
        opt(cpu, "Intel Core i7", "High performance", "CPU-I7", new BigDecimal("200"), 40, true, false, 1);
        opt(cpu, "AMD Ryzen 7", "Multi-threaded", "CPU-R7", new BigDecimal("150"), 35, true, false, 2);

        OptionGroup ram = group(p, "ram", "Memory (RAM)", "single", true, 1, 1, 1);
        opt(ram, "8 GB DDR5", "Entry level", "RAM-8", BigDecimal.ZERO, 80, true, true, 0);
        opt(ram, "16 GB DDR5", "Recommended", "RAM-16", new BigDecimal("80"), 60, true, false, 1);
        opt(ram, "32 GB DDR5", "Power users", "RAM-32", new BigDecimal("180"), 25, true, false, 2);

        OptionGroup storage = group(p, "storage", "Storage", "single", true, 1, 1, 2);
        opt(storage, "512 GB NVMe SSD", "Fast boot", "SSD-512", BigDecimal.ZERO, 100, true, true, 0);
        opt(storage, "1 TB NVMe SSD", "More space", "SSD-1T", new BigDecimal("100"), 70, true, false, 1);
        opt(storage, "2 TB NVMe SSD", "Maximum space", "SSD-2T", new BigDecimal("220"), 30, true, false, 2);

        OptionGroup display = group(p, "display", "Display", "single", true, 1, 1, 3);
        opt(display, "14\" FHD (1920x1080)", "Compact", "DISP-FHD14", BigDecimal.ZERO, 60, true, true, 0);
        opt(display, "15.6\" FHD (1920x1080)", "Standard", "DISP-FHD15", new BigDecimal("40"), 55, true, false, 1);
        ProductOption qhd = opt(display, "16\" QHD+ (2560x1600)", "Sharp and bright", "DISP-QHD16", new BigDecimal("140"), 25, true, false, 2);

        OptionGroup gpu = group(p, "gpu", "Graphics", "single", true, 1, 1, 4);
        ProductOption integrated = opt(gpu, "Integrated Graphics", "Everyday use", "GPU-IGPU", BigDecimal.ZERO, 90, true, true, 0);
        opt(gpu, "NVIDIA RTX 4060", "Gaming & creator", "GPU-4060", new BigDecimal("350"), 30, true, false, 1);
        opt(gpu, "NVIDIA RTX 4070", "High-end", "GPU-4070", new BigDecimal("550"), 15, true, false, 2);

        OptionGroup warranty = group(p, "warranty", "Warranty", "single", true, 1, 1, 5);
        opt(warranty, "1 Year Limited", "Standard", "WAR-1", BigDecimal.ZERO, 100, true, true, 0);
        opt(warranty, "2 Years Extended", "Peace of mind", "WAR-2", new BigDecimal("60"), 100, true, false, 1);
        opt(warranty, "3 Years Premium", "Full coverage", "WAR-3", new BigDecimal("110"), 100, true, false, 2);

        productRepository.save(p);

        ConflictRule rule = new ConflictRule();
        rule.setSourceOption(integrated);
        rule.setTargetOption(qhd);
        rule.setRuleType("EXCLUDES");
        conflictRuleRepository.save(rule);
    }

    private void seedBike() {
        Product p = new Product();
        p.setName("Trail Mountain Bike");
        p.setSlug("trail-mountain-bike");
        p.setDescription("Build your trail bike: frame, suspension, wheels and drivetrain.");
        p.setCategory("Sports");
        p.setImageUrl(image("trail-bike"));
        p.setBasePrice(new BigDecimal("1299.00"));

        OptionGroup frame = group(p, "frame", "Frame Size", "single", true, 1, 1, 0);
        opt(frame, "Small (S)", "Rider 155-165 cm", "FR-S", BigDecimal.ZERO, 20, true, false, 0);
        opt(frame, "Medium (M)", "Rider 165-175 cm", "FR-M", BigDecimal.ZERO, 25, true, true, 1);
        opt(frame, "Large (L)", "Rider 175-185 cm", "FR-L", new BigDecimal("50"), 15, true, false, 2);

        OptionGroup suspension = group(p, "suspension", "Suspension", "single", true, 1, 1, 1);
        opt(suspension, "Front fork 120mm", "Trail", "SUS-120", BigDecimal.ZERO, 30, true, true, 0);
        opt(suspension, "Front fork 140mm", "Aggressive trail", "SUS-140", new BigDecimal("120"), 20, true, false, 1);

        OptionGroup wheels = group(p, "wheels", "Wheels", "single", true, 1, 1, 2);
        opt(wheels, "27.5\" Alloy", "Agile", "WH-275", BigDecimal.ZERO, 30, true, true, 0);
        opt(wheels, "29\" Carbon", "Fast rolling", "WH-29C", new BigDecimal("250"), 12, true, false, 1);

        OptionGroup drivetrain = group(p, "drivetrain", "Drivetrain", "single", true, 1, 1, 3);
        opt(drivetrain, "Shimano Deore 1x12", "Reliable", "DT-DEORE", BigDecimal.ZERO, 35, true, true, 0);
        opt(drivetrain, "SRAM GX Eagle 1x12", "Premium shifting", "DT-GX", new BigDecimal("180"), 18, true, false, 1);

        productRepository.save(p);
    }

    private void seedCatalog() {
        List<String[]> products = List.of(
                new String[]{"Urban E-Bike", "urban-e-bike", "Commuter electric bike for the city.", "Sports", "urban-ebike", "999.00"},
                new String[]{"Smart Watch Series 5", "smart-watch-5", "Track health, fitness and notifications.", "Wearables", "smartwatch", "249.00"},
                new String[]{"Noise-Cancelling Headphones", "nc-headphones", "Immersive sound with active noise cancellation.", "Audio", "headphones", "179.00"},
                new String[]{"4K Action Camera", "action-camera", "Capture adventures in 4K with stabilization.", "Cameras", "actioncam", "299.00"},
                new String[]{"Portable Power Bank 20000mAh", "power-bank-20k", "Fast charge your devices anywhere.", "Accessories", "powerbank", "49.00"},
                new String[]{"Mechanical Keyboard TKL", "mech-keyboard-tkl", "Tactile switches and compact layout.", "Computing", "keyboard", "129.00"},
                new String[]{"Ergonomic Office Chair", "office-chair", "All-day comfort with lumbar support.", "Furniture", "chair", "349.00"},
                new String[]{"Standing Desk Converter", "standing-desk", "Switch between sitting and standing.", "Furniture", "desk", "199.00"},
                new String[]{"Smart Thermostat", "smart-thermostat", "Save energy with intelligent control.", "Smart Home", "thermostat", "159.00"},
                new String[]{"Robot Vacuum Cleaner", "robot-vacuum", "Automated cleaning with smart mapping.", "Smart Home", "vacuum", "399.00"},
                new String[]{"Air Purifier HEPA", "air-purifier", "Clean indoor air with HEPA filtration.", "Smart Home", "purifier", "189.00"},
                new String[]{"Espresso Machine Pro", "espresso-pro", "Barista-quality coffee at home.", "Kitchen", "espresso", "499.00"},
                new String[]{"Blender High Speed", "blender-hs", "Crush ice and blend smoothies easily.", "Kitchen", "blender", "119.00"},
                new String[]{"Digital Drawing Tablet", "drawing-tablet", "Precise pen input for artists.", "Computing", "tablet", "219.00"},
                new String[]{"Gaming Monitor 27\" 144Hz", "gaming-monitor-27", "Smooth gameplay with high refresh rate.", "Computing", "monitor", "329.00"},
                new String[]{"Wireless Ergonomic Mouse", "wireless-mouse", "Comfortable all-day pointing device.", "Accessories", "mouse", "59.00"},
                new String[]{"Travel Backpack 40L", "travel-backpack-40", "Carry-on sized adventure backpack.", "Accessories", "backpack", "89.00"},
                new String[]{"LED Ring Light 18\"", "ring-light-18", "Perfect lighting for video calls.", "Accessories", "ringlight", "79.00"}
        );

        for (String[] spec : products) {
            Product p = new Product();
            p.setName(spec[0]);
            p.setSlug(spec[1]);
            p.setDescription(spec[2]);
            p.setCategory(spec[3]);
            p.setImageUrl(image(spec[4]));
            p.setBasePrice(new BigDecimal(spec[5]));
            productRepository.save(p);
        }
    }

    private OptionGroup group(Product p, String name, String display, String type, boolean required,
                             int min, int max, int sort) {
        OptionGroup g = new OptionGroup();
        g.setProduct(p);
        g.setName(name);
        g.setDisplayName(display);
        g.setSelectionType(type);
        g.setRequired(required);
        g.setMinSelections(min);
        g.setMaxSelections(max);
        g.setSortOrder(sort);
        p.getOptionGroups().add(g);
        return g;
    }

    private ProductOption opt(OptionGroup g, String name, String desc, String sku, BigDecimal delta,
                             int stock, boolean active, boolean defaultOption, int sort) {
        ProductOption o = new ProductOption();
        o.setOptionGroup(g);
        o.setName(name);
        o.setDescription(desc);
        o.setSku(sku);
        o.setPriceDelta(delta);
        o.setStock(stock);
        o.setActive(active);
        o.setDefaultOption(defaultOption);
        o.setSortOrder(sort);
        g.getOptions().add(o);
        return o;
    }
}
