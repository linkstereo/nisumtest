package com.nisum.errorhandling.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorBody {

    private String mensaje;
}
