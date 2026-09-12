package com.iwfc.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class Repository<T> {
    private final Map<String, T> storage = new HashMap<>();
    private final Function<T, String> keyExtractor;

    public Repository(Function<T, String> keyExtractor) {
        this.keyExtractor = keyExtractor;
    }

    public void save(T entity) {
        String key = keyExtractor.apply(entity);
        storage.put(key, entity);
    }

    public Optional<T> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    public boolean existsById(String id) {
        return storage.containsKey(id);
    }

    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    public void deleteById(String id) {
        storage.remove(id);
    }
}
