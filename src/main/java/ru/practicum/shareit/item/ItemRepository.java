package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    @Query(value = "SELECT i FROM Item i WHERE " +
            "LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%')) AND " +
            "i.available IS TRUE ")
    Page<Item> findAllByDescriptionOrNameContainsIgnoreCase(String text, Pageable page);

    @Query(value = "SELECT i FROM Item i " +
            "JOIN FETCH i.owner " +
            "LEFT JOIN i.comments " +
            "WHERE i.id = :id")
    Optional<Item> getItemByIdWithOwner(Integer id);

    @Query(value = "SELECT i FROM Item i " +
            "JOIN i.owner " +
            "LEFT JOIN i.comments " +
            "WHERE i.owner.id = :id")
    Page<Item> findAllByOwnerIdOrderById(Integer id, Pageable page);
}
