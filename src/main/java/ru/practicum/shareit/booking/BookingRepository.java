package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer>, QuerydslPredicateExecutor<Booking> {

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN FETCH b.item " +
            "JOIN FETCH b.booker " +
            "WHERE b.id=:id")
    Optional<Booking> findByIdWithBookerAndItem(Integer id);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN FETCH b.booker " +
            "WHERE b.item.id = :itemId AND " +
            "b.status = 'APPROVED' " +
            "ORDER BY b.start")
    List<Booking> findAllByItem_Id(Integer itemId);

    @Query(value = "SELECT COUNT(b) FROM Booking b " +
            "WHERE b.booker.id = :bookerId AND " +
            "b.item.id = :itemId AND " +
            "b.status = 'APPROVED' AND " +
            "b.start < :now")
    Integer countByBookerIdAndItemId(Integer bookerId, Integer itemId, LocalDateTime now);
}
