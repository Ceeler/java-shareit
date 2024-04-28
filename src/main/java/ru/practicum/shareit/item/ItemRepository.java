package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    @Query(value = "SELECT i FROM Item i WHERE " +
            "LOWER(i.name) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', ?1, '%')) AND " +
            "i.available IS TRUE")
    List<Item> findAllByDescriptionOrNameContainsIgnoreCase(String text);

    @Query(value = "SELECT i FROM Item i " +
            "JOIN FETCH i.owner " +
            "LEFT JOIN FETCH i.comments " +
            "WHERE i.id = :id")
    Optional<Item> getItemByIdWithOwner(Integer id);

    @Query(value = "SELECT i FROM Item i " +
            "JOIN FETCH i.owner " +
            "LEFT JOIN FETCH i.comments " +
            "WHERE i.owner.id = :id")
    List<Item> findAllByOwnerIdOrderById(Integer id);
}
