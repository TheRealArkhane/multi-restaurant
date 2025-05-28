package com.testcase.kitchenservice.mapstruct.mapper;

import com.testcase.commondto.waiterservice.MenuDTO;
import com.testcase.commondto.waiterservice.OrderPositionDTO;
import com.testcase.commondto.waiterservice.OrderValidationDTO;
import com.testcase.grpc.GrpcMenuDTOProto;
import com.testcase.grpc.GrpcOrderPositionDTOProto;
import com.testcase.grpc.GrpcOrderValidationDTOProto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;
import java.util.Set;

/**
 * Маппер для преобразования gRPC DTO объектов в внутренние DTO,
 * используемые в бизнес-логике для валидации заказа.
 */
@Mapper(componentModel = "spring")
public interface GrpcOrderValidationFromGrpcMapper {


    /**
     * Преобразует gRPC DTO заказа в внутренний DTO для валидации.
     *
     * @param proto gRPC представление заказа
     * @return внутренний DTO для валидации заказа
     */
    @Mappings({
            @Mapping(source = "orderId", target = "orderId"),
            @Mapping(source = "positionsList", target = "positions")
    })
    OrderValidationDTO fromGrpc(GrpcOrderValidationDTOProto proto);


    /**
     * Преобразует gRPC DTO позиции заказа в внутренний DTO.
     *
     * @param proto gRPC представление позиции заказа
     * @return внутренний DTO позиции заказа
     */
    @Mappings({
            @Mapping(source = "orderId", target = "orderId"),
            @Mapping(source = "quantity", target = "quantity"),
            @Mapping(source = "menu", target = "menu")
    })
    OrderPositionDTO fromGrpc(GrpcOrderPositionDTOProto proto);


    /**
     * Преобразует gRPC DTO блюда (меню) в внутренний DTO.
     *
     * @param proto gRPC представление блюда
     * @return внутренний DTO блюда
     */
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "cost", target = "cost")
    })
    MenuDTO fromGrpc(GrpcMenuDTOProto proto);


    /**
     * Преобразует список gRPC DTO позиций заказа в множество внутренних DTO.
     *
     * @param list список gRPC представлений позиций
     * @return множество внутренних DTO позиций заказа
     */
    Set<OrderPositionDTO> fromGrpc(List<GrpcOrderPositionDTOProto> list);
}
