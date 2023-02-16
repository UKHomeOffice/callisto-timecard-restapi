package uk.gov.homeoffice.digital.sas.timecard.listeners;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import uk.gov.homeoffice.digital.sas.timecard.kafka.producers.KafkaProducerService;
import uk.gov.homeoffice.digital.sas.timecard.model.TimeEntry;
import uk.gov.homeoffice.digital.sas.timecard.testutils.CommonUtils;
import uk.gov.homeoffice.digital.sas.timecard.testutils.TimeEntryFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
@WebAppConfiguration
@AutoConfigureMockMvc(addFilters = true)
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092",
    "port=9092" })
public class KafkaEntityListenerIt {

  @Autowired
  private MockMvc mockMvc;

  private TimeEntry timeEntry;

  @MockBean
  private KafkaProducerService kafkaProducerService;

  @MockBean
  private KafkaEntityListener kafkaEntityListener;

  private UUID tenantId;

  private UUID ownerId;

  private String messageKey;

  @BeforeEach
  void setUp() {
    ownerId = UUID.randomUUID();
    timeEntry = TimeEntryFactory.createTimeEntry(ownerId,
        CommonUtils.getAsDate(LocalDateTime.now()));
    tenantId = timeEntry.getTenantId();
    messageKey = CommonUtils.generateMessageKey(timeEntry);
    TransactionSynchronizationManager.initSynchronization();
    kafkaEntityListener.createProducerService(kafkaProducerService);
  }

  @Test
  public void givenValidRequest_WhenSendingCreateRequest_thenTransactionSyncLogsSuccessMessage()
      throws Exception {
    ListAppender<ILoggingEvent> listAppender = getLoggingEventListAppender();

    List<ILoggingEvent> logList = listAppender.list;

    createTimeEntry();

    assertEquals(String.format(
        "Database transaction [ create ] with ownerId [ %s ] was successful",
        timeEntry.getOwnerId().toString()), logList.get(0).getMessage());

    assertEquals(String.format(
        "Transaction successful with messageKey [ %s ]", messageKey), logList.get(1).getMessage());
  }

  @Test
  public void givenValidRequest_WhenSendingUpdateRequest_thenTransactionSyncLogsSuccessMessage()
    throws Exception {
    ListAppender<ILoggingEvent> listAppender = getLoggingEventListAppender();

    List<ILoggingEvent> logList = listAppender.list;

    MvcResult result = createTimeEntry();
    String content = result.getResponse().getContentAsString();
    String id = content.substring(38, 74);

    TimeEntry timeEntryUpdate = TimeEntryFactory.createTimeEntry(ownerId, tenantId,
        CommonUtils.getAsDate(LocalDateTime.now().plusHours(1)));

    mockMvc.perform(put("/resources/time-entries/"+ id + "?tenantId=" + tenantId)
          .contentType(MediaType.APPLICATION_JSON)
          .content(CommonUtils.timeEntryAsJsonString(timeEntryUpdate)))
        .andDo(print())
        .andExpect(status().isOk());

    assertEquals(String.format(
        "Database transaction [ update ] with ownerId [ %s ] was successful",
        timeEntry.getOwnerId().toString()), logList.get(2).getMessage());

    assertEquals(String.format(
        "Transaction successful with messageKey [ %s ]", messageKey), logList.get(3).getMessage());
  }

  @Test
  public void givenValidRequest_WhenSendingDelete_thenTransactionSyncLogsSuccessMessage()
    throws Exception {
    ListAppender<ILoggingEvent> listAppender = getLoggingEventListAppender();

    List<ILoggingEvent> logList = listAppender.list;

    MvcResult result = createTimeEntry();
    String content = result.getResponse().getContentAsString();
    String id = content.substring(38, 74);

    mockMvc.perform(delete("/resources/time-entries/"+ id + "?tenantId=" + tenantId))
        .andDo(print())
        .andExpect(status().isOk());

    assertEquals(String.format(
        "Database transaction [ create ] with ownerId [ %s ] was successful",
        timeEntry.getOwnerId().toString()), logList.get(0).getMessage());

    assertEquals(String.format(
        "Transaction successful with messageKey [ %s ]", messageKey), logList.get(1).getMessage());
  }


  private MvcResult createTimeEntry() throws Exception {
    return mockMvc.perform(post("/resources/time-entries?tenantId=" + tenantId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(CommonUtils.timeEntryAsJsonString(timeEntry)))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();
  }

  @NotNull
  private static ListAppender<ILoggingEvent> getLoggingEventListAppender() {
    Logger kafkaLogger = (Logger) LoggerFactory.getLogger(KafkaEntityListener.class);

    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();

    kafkaLogger.addAppender(listAppender);
    return listAppender;
  }

  @AfterEach
  void clear() {
    TransactionSynchronizationManager.clear();
  }

}
