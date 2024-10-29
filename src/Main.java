import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static Map<String, Map<String, String>> cache = new HashMap<>();
    private static final String FILE_PATH = "initialData.txt";

    public static void main(String[] args) {
        System.out.println("Выберите метод инициализации кэша:");
        System.out.println("1 - Предопределенные данные");
        System.out.println("2 - Ввод через консоль");
        System.out.println("3 - Загрузка из текстового файла");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                initializeCacheWithDefaultData();
                break;
            case 2:
                initializeCacheFromConsole();
                break;
            case 3:
                initializeCacheFromFile();
                break;
            default:
                System.out.println("Неверный выбор. Программа завершена.");
                return;
        }

        while (true) {
            System.out.print("Введите команду (add <objectId> <propertyId> <value>, get <objectId> <propertyId> или значение для обновления): ");
            String input = scanner.nextLine();

            if (input.equals("error")) {
                System.out.println("Ошибка обнаружена. Повторная инициализация кэша через 30 секунд...");
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                switch (choice) {
                    case 1:
                        initializeCacheWithDefaultData();
                        break;
                    case 2:
                        initializeCacheFromConsole();
                        break;
                    case 3:
                        initializeCacheFromFile();
                        break;
                }
                continue;
            }

            String[] parts = input.split(" ");
            if (parts.length == 4 && parts[0].equalsIgnoreCase("add")) {
                String objectId = parts[1];
                String propertyId = parts[2];
                String value = parts[3];
                addToCache(objectId, propertyId, value);
            } else if (parts.length == 3 && parts[0].equalsIgnoreCase("get")) {
                String objectId = parts[1];
                String propertyId = parts[2];
                String value = getValue(objectId, propertyId);
                if (value != null) {
                    System.out.println("Значение [" + objectId + ", " + propertyId + "]: " + value);
                } else {
                    System.out.println("Значение не найдено.");
                }
            } else if (parts.length == 2) {
                String objectId = parts[0];
                String newValue = parts[1];
                updateCache(objectId, newValue);
            } else {
                System.out.println("Некорректная команда. Попробуйте снова.");
            }
        }
    }

    private static void initializeCacheWithDefaultData() {
        cache.clear();
        System.out.println("Инициализация кэша с предопределенными данными...");
        cache.computeIfAbsent("1", k -> new HashMap<>()).put("name", "John");
        cache.computeIfAbsent("1", k -> new HashMap<>()).put("age", "30");
        cache.computeIfAbsent("2", k -> new HashMap<>()).put("name", "Jane");
        cache.computeIfAbsent("2", k -> new HashMap<>()).put("age", "25");
        System.out.println("Кэш успешно инициализирован.");
    }

    private static void initializeCacheFromConsole() {
        cache.clear();
        System.out.println("Введите начальные данные (в формате objectId,propertyId,value) или 'done' для завершения:");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("done")) break;

            String[] parts = input.split(",");
            if (parts.length == 3) {
                String objectId = parts[0].trim();
                String propertyId = parts[1].trim();
                String value = parts[2].trim();
                cache.computeIfAbsent(objectId, k -> new HashMap<>()).put(propertyId, value);
            } else {
                System.out.println("Некорректный формат. Попробуйте снова.");
            }
        }
        System.out.println("Кэш успешно инициализирован.");
    }

    private static void initializeCacheFromFile() {
        cache.clear();
        System.out.println("Инициализация кэша из текстового файла...");
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String objectId = parts[0].trim();
                    String propertyId = parts[1].trim();
                    String value = parts[2].trim();
                    cache.computeIfAbsent(objectId, k -> new HashMap<>()).put(propertyId, value);
                }
            }
            System.out.println("Кэш успешно инициализирован из файла.");
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }

    private static String getValue(String objectId, String propertyId) {
        Map<String, String> properties = cache.get(objectId);
        return properties != null ? properties.get(propertyId) : null;
    }

    private static void updateCache(String objectId, String newValue) {
        Map<String, String> properties = cache.get(objectId);
        if (properties != null) {
            for (String key : properties.keySet()) {
                properties.put(key, newValue);
            }
            System.out.println("Кэш обновлен для объекта " + objectId);
        } else {
            System.out.println("Объект " + objectId + " не найден в кэше.");
        }
    }

    private static void addToCache(String objectId, String propertyId, String value) {
        cache.computeIfAbsent(objectId, k -> new HashMap<>()).put(propertyId, value);
        System.out.println("Объект добавлен в кэш: [" + objectId + ", " + propertyId + "] = " + value);
    }
}
