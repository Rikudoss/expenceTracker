package kz.rikudo.study.expencetracker.mapper;

import kz.rikudo.study.expencetracker.dto.TransactionRequestDTO;
import kz.rikudo.study.expencetracker.dto.TransactionResponseDTO;
import kz.rikudo.study.expencetracker.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    // Убираем INSTANCE

    Transaction toEntity(TransactionRequestDTO dto);

    TransactionResponseDTO toDTO(Transaction entity);
}
