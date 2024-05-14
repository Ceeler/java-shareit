package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    @Query(value = "SELECT ir FROM ItemRequest ir " +
            "LEFT JOIN ir.items " +
            "WHERE ir.author.id=:userId " +
            "ORDER BY ir.createdAt DESC")
    List<ItemRequest> getAllByUserIdWithItems(Integer userId);

    @Query(value = "SELECT ir FROM ItemRequest ir " +
            "LEFT JOIN ir.items " +
            "WHERE ir.author.id != :userId")
    Page<ItemRequest> getAllWithItems(Integer userId, Pageable page);

    @Query(value = "SELECT ir FROM ItemRequest ir " +
            "LEFT JOIN ir.items " +
            "WHERE ir.id=:id")
    Optional<ItemRequest> getByIdWithItems(Integer id);
}
