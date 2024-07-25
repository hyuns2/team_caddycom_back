package com.flash21.caddycom.service.golfField;

import com.flash21.caddycom.global.common.FileUploader;
import com.flash21.caddycom.repository.account.AccountRepository;
import com.flash21.caddycom.repository.golfField.GolfFieldRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GolfFieldServiceTest {
    @InjectMocks
    private FileUploader fileUploader;
    @InjectMocks
    private GolfFieldRepository golfFieldRepository;
    @InjectMocks
    private FacilityService facilityService;
    @InjectMocks
    private AccountRepository accountRepository;
    @Mock
    private GolfFieldService golfFieldService;


}
