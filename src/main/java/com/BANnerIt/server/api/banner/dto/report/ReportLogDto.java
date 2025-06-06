package com.BANnerIt.server.api.banner.dto.report;

import com.BANnerIt.server.api.banner.domain.ReportStatus;
import com.BANnerIt.server.api.banner.dto.banner.BannerDetailsWithIdDto;

import java.time.ZonedDateTime;
import java.util.List;

public record ReportLogDto(Long report_id, ZonedDateTime report_time,
                           ReportStatus status, Long created_user_id,
                           List<String> images, LocationDto location,
                           String content, List<BannerDetailsWithIdDto> banner_info) {
}
