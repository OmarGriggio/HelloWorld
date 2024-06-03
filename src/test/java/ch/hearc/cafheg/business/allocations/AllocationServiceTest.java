package ch.hearc.cafheg.business.allocations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ch.hearc.cafheg.business.common.Montant;
import ch.hearc.cafheg.infrastructure.persistance.AllocataireMapper;
import ch.hearc.cafheg.infrastructure.persistance.AllocationMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AllocationServiceTest {

  private static final String PARENT_1 = "Parent1";
  private static final String PARENT_2 = "Parent2";
          ;
  private AllocataireMapper allocataireMapper = mock(AllocataireMapper.class);
  private AllocationMapper allocationMapper = mock(AllocationMapper.class);
  private AllocationService allocationService = spy(new AllocationService(allocataireMapper, allocationMapper));

  private ParentDroitAllocationParams params;

  @BeforeEach
  void setUp() {
    allocataireMapper = mock(AllocataireMapper.class);
    allocationMapper = mock(AllocationMapper.class);

    allocationService = new AllocationService(allocataireMapper, allocationMapper);

    // Default params setup
    params = new ParentDroitAllocationParams.Builder()
            .setEnfantResidence("Neuchâtel")
            .setParent1Residence("Neuchâtel")
            .setParent2Residence("Neuchâtel")
            .setParent1ActiviteLucrative(true)
            .setParent2ActiviteLucrative(true)
            .setParent1Salaire(BigDecimal.valueOf(2500))
            .setParent2Salaire(BigDecimal.valueOf(3000))
            .build();

  }

  @Test
  void findAllAllocataires_GivenEmptyAllocataires_ShouldBeEmpty() {
    Mockito.when(allocataireMapper.findAll("Geiser")).thenReturn(Collections.emptyList());
    List<Allocataire> all = allocationService.findAllAllocataires("Geiser");
    assertThat(all).isEmpty();
  }

  @Test
  void findAllAllocataires_Given2Geiser_ShouldBe2() {
    Mockito.when(allocataireMapper.findAll("Geiser"))
            .thenReturn(Arrays.asList(new Allocataire(new NoAVS("1000-2000"), "Geiser", "Arnaud"),
                    new Allocataire(new NoAVS("1000-2001"), "Geiser", "Aurélie")));
    List<Allocataire> all = allocationService.findAllAllocataires("Geiser");
    assertAll(() -> assertThat(all.size()).isEqualTo(2),
            () -> assertThat(all.get(0).getNoAVS()).isEqualTo(new NoAVS("1000-2000")),
            () -> assertThat(all.get(0).getNom()).isEqualTo("Geiser"),
            () -> assertThat(all.get(0).getPrenom()).isEqualTo("Arnaud"),
            () -> assertThat(all.get(1).getNoAVS()).isEqualTo(new NoAVS("1000-2001")),
            () -> assertThat(all.get(1).getNom()).isEqualTo("Geiser"),
            () -> assertThat(all.get(1).getPrenom()).isEqualTo("Aurélie"));
  }

  @Test
  void findAllocationsActuelles() {
    Mockito.when(allocationMapper.findAll())
            .thenReturn(Arrays.asList(new Allocation(new Montant(new BigDecimal(1000)), Canton.NE,
                    LocalDate.now(), null), new Allocation(new Montant(new BigDecimal(2000)), Canton.FR,
                    LocalDate.now(), null)));
    List<Allocation> all = allocationService.findAllocationsActuelles();
    assertAll(() -> assertThat(all.size()).isEqualTo(2),
            () -> assertThat(all.get(0).getMontant()).isEqualTo(new Montant(new BigDecimal(1000))),
            () -> assertThat(all.get(0).getCanton()).isEqualTo(Canton.NE),
            () -> assertThat(all.get(0).getDebut()).isEqualTo(LocalDate.now()),
            () -> assertThat(all.get(0).getFin()).isNull(),
            () -> assertThat(all.get(1).getMontant()).isEqualTo(new Montant(new BigDecimal(2000))),
            () -> assertThat(all.get(1).getCanton()).isEqualTo(Canton.FR),
            () -> assertThat(all.get(1).getDebut()).isEqualTo(LocalDate.now()),
            () -> assertThat(all.get(1).getFin()).isNull());
  }

  @Test
  @DisplayName("Test: Parent 1 is active and not parent 2, should be P1")
  public void getParentDroitAllocation_GivenParent1Active_ShouldReturnParent1() {
    params.setParent2ActiviteLucrative(false);
    assertEquals(PARENT_1, allocationService.getParentDroitAllocation(params));
  }

  @Test
  @DisplayName("Test: Parent 1 is NOT active and parent 2 IS, should be P2")
  public void getParentDroitAllocation_GivenParent2Active_ShouldReturnParent2() {
    params.setParent1ActiviteLucrative(false);
    assertEquals(PARENT_2, allocationService.getParentDroitAllocation(params));
  }

  @Test
  @DisplayName("Test: Parent 2 active in Bienne, should be P2")
  public void getParentDroitAllocation_Parent2_Bienne_Bienne() {
    params.setParent2Residence("Bienne");
    assertEquals(PARENT_2, allocationService.getParentDroitAllocation(params));
  }

  @Test
  @DisplayName("Test: Should throw IllegalArgumentException when Enfant Residence is null")
  public void testGetParentDroitAllocation_ThrowsIllegalArgumentException_NullEnfantResidence() {
    params.setEnfantResidence(null);
    assertThrows(IllegalArgumentException.class, () -> allocationService.getParentDroitAllocation(params));
  }

  @Test
  @DisplayName("Test: Should throw IllegalArgumentException when Enfant Residence is empty")
  public void testGetParentDroitAllocation_ThrowsIllegalArgumentException_EmptyEnfantResidence() {
    params.setEnfantResidence("");
    assertThrows(IllegalArgumentException.class, () -> allocationService.getParentDroitAllocation(params));
  }

  @Test
  @DisplayName("Test: Should throw IllegalArgumentException when Parent 1 Residence is null")
  public void testGetParentDroitAllocation_ThrowsIllegalArgumentException_NullParent1Residence() {
    params.setParent1Residence(null);
    assertThrows(IllegalArgumentException.class, () -> allocationService.getParentDroitAllocation(params));
  }

  @Test
  @DisplayName("Test: Should throw IllegalArgumentException when Parent 1 Residence is empty")
  public void testGetParentDroitAllocation_ThrowsIllegalArgumentException_EmptyParent1Residence() {
    params.setParent1Residence("");
    assertThrows(IllegalArgumentException.class, () -> allocationService.getParentDroitAllocation(params));
  }

  @Test
  @DisplayName("Test: Should throw IllegalArgumentException when Parent 2 Residence is empty")
  public void testGetParentDroitAllocation_ThrowsIllegalArgumentException_EmptyParent2Residence() {
    params.setParent2Residence("");
    assertThrows(IllegalArgumentException.class, () -> allocationService.getParentDroitAllocation(params));
  }

  @Test
  @DisplayName("Test: Should throw IllegalArgumentException when Parent 2 Activité Lucrative is null")
  public void testGetParentDroitAllocation_ThrowsIllegalArgumentException_NullParent2ActiviteLucrative() {
    params.setParent2ActiviteLucrative(null);
    assertThrows(IllegalArgumentException.class, () -> allocationService.getParentDroitAllocation(params));
  }

  @Test
  @DisplayName("Test: Should throw IllegalArgumentException when Parent 1 Salary is null")
  public void testGetParentDroitAllocation_ThrowsIllegalArgumentException_NullParent1Salaire() {
    params.setParent1Salaire(null);
    assertThrows(IllegalArgumentException.class, () -> allocationService.getParentDroitAllocation(params));
  }

  @Test
  @DisplayName("Test: Should throw IllegalArgumentException when Parent 2 Salary is null")
  public void testGetParentDroitAllocation_ThrowsIllegalArgumentException_NullParent2Salaire() {
    params.setParent2Salaire(null);
    assertThrows(IllegalArgumentException.class, () -> allocationService.getParentDroitAllocation(params));
  }

  @Test
  @DisplayName("Test: Parent 1 salary less than 2000, should be P1")
  public void testGetParentDroitAllocation_ReturnsParent1_Parent1SalaireLessThan2000() {
    params.setParent1Salaire(BigDecimal.valueOf(1999));
    assertEquals(PARENT_1, allocationService.getParentDroitAllocation(params));
  }

  @Test
  @DisplayName("Test: Parent 2 salary less than 2000, should be P2")
  public void testGetParentDroitAllocation_ReturnsParent2_Parent2SalaireLessThan2000() {
    params.setParent2Salaire(BigDecimal.valueOf(1999));
    assertEquals(PARENT_2, allocationService.getParentDroitAllocation(params));
  }

  @Test
  @DisplayName("Test: Parent 1 salary less than 2000 and Parent 2 salary greater than or equal to 2000, should be P1")
  public void testGetParentDroitAllocation_ReturnsParent1_Parent1SalaireLessThan2000_Parent2SalaireGreaterThanOrEqualTo2000() {
    params.setParent1Salaire(BigDecimal.valueOf(1999));
    params.setParent2Salaire(BigDecimal.valueOf(2000));
    assertEquals(PARENT_1, allocationService.getParentDroitAllocation(params));
  }

  @Test
  @DisplayName("Test: Parent 1 not active and Parent 2 active, should be P2")
  public void testGetParentDroitAllocation_ReturnsParent2_Parent1ActiviteLucrativeFalse_Parent2ActiviteLucrativeTrue() {
    params.setParent1ActiviteLucrative(false);
    assertEquals(PARENT_2, allocationService.getParentDroitAllocation(params));
  }

  @Test
  @DisplayName("Test: Parent 1 active and Parent 2 not active, should be P1")
  public void testGetParentDroitAllocation_ReturnsParent1_Parent1ActiviteLucrativeTrue_Parent2ActiviteLucrativeFalse() {
    params.setParent2ActiviteLucrative(false);
    assertEquals(PARENT_1, allocationService.getParentDroitAllocation(params));
  }
  @Test
  @DisplayName("Branch a: Parent 1 has activité lucrative and not Parent 2, should be P1")
  public void getParentDroitAllocation_BranchA() {
    params.setParent2ActiviteLucrative(false);
    assertEquals(PARENT_1, allocationService.getParentDroitAllocation(params));
  }

  @Test
  @DisplayName("Branch b: Both parents have activité lucrative, Parent 1 has authority, should be P1")
  public void getParentDroitAllocation_BranchB() {
    params.setParent2ActiviteLucrative(true);
    // Assume there is a method to set parental authority
    params.setParent2ParentalAuthority(false);
    assertEquals(PARENT_1, allocationService.getParentDroitAllocation(params));
  }

  @Test
  @DisplayName("Branch c: Both parents have activité lucrative, live separately, child with Parent 1, should be P1")
  public void getParentDroitAllocation_BranchC() {
    params.setParent2ActiviteLucrative(true);
    params.setParentsTogether(false);
    params.setParent2Residence("Bienne");
    assertEquals(PARENT_1, allocationService.getParentDroitAllocation(params));
  }

  @Test
  @DisplayName("Branch d: Both parents have activité lucrative, live together, Parent 1 works in child's canton, should be P1")
  public void getParentDroitAllocation_BranchD() {
    params.setParent2ActiviteLucrative(true);
    params.setParentsTogether(true);
    params.setParent2WorkInChildCanton(false);
    assertEquals(PARENT_1, allocationService.getParentDroitAllocation(params));
  }

  @Test
  @DisplayName("Branch e: Both parents have activité lucrative, live together, both salaried, Parent 1 has higher AVS income, should be P1")
  public void getParentDroitAllocation_BranchE() {
    params.setParent2ActiviteLucrative(true);
    params.setParentsTogether(true);
    params.setParent1Salaire(BigDecimal.valueOf(5000));
    params.setParent2Salaire(BigDecimal.valueOf(3000));
    assertEquals(PARENT_1, allocationService.getParentDroitAllocation(params));
  }

  @Test
  @DisplayName("Branch f: Both parents are independent, Parent 2 has higher AVS income, should be P2")
  public void getParentDroitAllocation_BranchF() {
    params.setParent2ActiviteLucrative(true);
    params.setParentsTogether(true);
    params.setParent1Salaire(BigDecimal.valueOf(3000));
    params.setParent2Salaire(BigDecimal.valueOf(5000));
    assertEquals(PARENT_2, allocationService.getParentDroitAllocation(params));
  }

}
  /*
  // Headers de la requête HTTP doit contenir "Content-Type: application/json"
  // BODY de la requête HTTP à transmettre afin de tester le endpoint
  {
      "enfantResidence" : "Neuchâtel",
      "parent1Residence" : "Neuchâtel",
      "parent2Residence" : "Bienne",
      "parent1ActiviteLucrative" : true,
      "parent2ActiviteLucrative" : true,
      "parent1Salaire" : 2500,
      "parent2Salaire" : 3000
  }
   */