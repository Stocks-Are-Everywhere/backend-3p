package org.scoula.three_people.global.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdDateTime;

	@LastModifiedDate
	@Column(nullable = false)
	private LocalDateTime updatedDateTime;

	@Column(nullable = true)
	@Builder.Default
	private LocalDateTime deletedDateTime = null;

	public boolean isDeleted() {
		return deletedDateTime != null;
	}

	public void softDelete(final LocalDateTime deletedDateTime) {
		if (deletedDateTime == null) {
			throw new IllegalArgumentException("deletedDateTime must not be `null`");
		}
		this.deletedDateTime = deletedDateTime;
	}

}
