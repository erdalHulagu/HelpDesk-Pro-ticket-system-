
package com.erdal.helpdeskpro.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.erdal.helpdeskpro.domain.TicketActivity;
import com.erdal.helpdeskpro.dtos.TicketActivityDTO;

public class TicketActivityMapper {

    public TicketActivityDTO toDTO(TicketActivity activity) {

        if (activity == null) {
            return null;
        }

        TicketActivityDTO dto = new TicketActivityDTO();

        dto.setId(activity.getId());
        dto.setTicketId(activity.getTicket().getId());
        dto.setActivityType(activity.getActivityType());
        dto.setDescription(activity.getDescription());
        dto.setOldValue(activity.getOldValue());
        dto.setNewValue(activity.getNewValue());
        dto.setCreatedAt(activity.getCreatedAt());

        if (activity.getPerformedBy() != null) {
            dto.setPerformedById(activity.getPerformedBy().getId());
            dto.setPerformedByUsername(
                    activity.getPerformedBy().getUsername()
            );
        }

        return dto;
    }

    public List<TicketActivityDTO> toDTOList(
            List<TicketActivity> activities) {

        return activities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
