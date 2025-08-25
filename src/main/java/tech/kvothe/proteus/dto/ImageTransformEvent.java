package tech.kvothe.proteus.dto;

import org.imgscalr.Scalr;
import tech.kvothe.proteus.dataModels.TransformationData;

public record ImageTransformEvent(TransformationData.Resize resize,
                                  TransformationData.Crop crop,
                                  Scalr.Rotation rotate,
                                  String format,
                                  TransformationData.Filters filters,
                                  Long imageId,
                                  String userEmail) {
}
