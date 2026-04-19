package com.api.bkhouse.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "clicked_info_view")
public class ClickedInfoView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "real_estate_post_id")
    @NotNull
    @NotBlank
    private UUID realEstatePostId;

    @Column(name = "create_by")
    @NotNull
    @NotBlank
    private UUID createBy;

    @Column(name = "create_at")
    @NotNull
    @NotBlank
    private Instant createAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getRealEstatePostId() {
        return realEstatePostId;
    }

    public void setRealEstatePostId(UUID realEstatePostId) {
        this.realEstatePostId = realEstatePostId;
    }

    public UUID getCreateBy() {
        return createBy;
    }

    public void setCreateBy(UUID createBy) {
        this.createBy = createBy;
    }

    public Instant getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Instant createAt) {
        this.createAt = createAt;
    }
}
