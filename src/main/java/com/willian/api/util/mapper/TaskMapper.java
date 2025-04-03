package com.willian.api.util.mapper;

import com.willian.api.model.TaskModelRequest;
import com.willian.api.model.TaskModelResponse;
import com.willian.api.persistence.entity.TaskEntity;

import java.util.List;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskModelResponse toModelResponse(TaskEntity employee);
    List<TaskModelResponse> toModelResponseList(List<TaskEntity> employees);

    TaskEntity toEntity(TaskModelRequest taskModelRequest);
    TaskEntity toEntity(TaskModelResponse taskModelRequest);
}
