package com.chananel.order.web.rest;

import com.chananel.order.OrderApp;

import com.chananel.order.config.SecurityBeanOverrideConfiguration;

import com.chananel.order.service.mapper.TestaMapper;
import com.chananel.order.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the TestaResource REST controller.
 *
 * @see TestaResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {OrderApp.class, SecurityBeanOverrideConfiguration.class})
public class TestaResourceIntTest {

    private static final String DEFAULT_NAME_1 = "AAAAAAAAAA";
    private static final String UPDATED_NAME_1 = "BBBBBBBBBB";

    @Autowired
    private TestaRepository testaRepository;

    @Autowired
    private TestaMapper testaMapper;

    @Autowired
    private TestaService testaService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restTestaMockMvc;

    private Testa testa;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TestaResource testaResource = new TestaResource(testaService);
        this.restTestaMockMvc = MockMvcBuilders.standaloneSetup(testaResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Testa createEntity(EntityManager em) {
        Testa testa = new Testa()
            .name1(DEFAULT_NAME_1);
        return testa;
    }

    @Before
    public void initTest() {
        testa = createEntity(em);
    }

    @Test
    @Transactional
    public void createTesta() throws Exception {
        int databaseSizeBeforeCreate = testaRepository.findAll().size();

        // Create the Testa
        TestaDTO testaDTO = testaMapper.toDto(testa);
        restTestaMockMvc.perform(post("/api/testas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(testaDTO)))
            .andExpect(status().isCreated());

        // Validate the Testa in the database
        List<Testa> testaList = testaRepository.findAll();
        assertThat(testaList).hasSize(databaseSizeBeforeCreate + 1);
        Testa testTesta = testaList.get(testaList.size() - 1);
        assertThat(testTesta.getName1()).isEqualTo(DEFAULT_NAME_1);
    }

    @Test
    @Transactional
    public void createTestaWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = testaRepository.findAll().size();

        // Create the Testa with an existing ID
        testa.setId(1L);
        TestaDTO testaDTO = testaMapper.toDto(testa);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTestaMockMvc.perform(post("/api/testas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(testaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Testa> testaList = testaRepository.findAll();
        assertThat(testaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllTestas() throws Exception {
        // Initialize the database
        testaRepository.saveAndFlush(testa);

        // Get all the testaList
        restTestaMockMvc.perform(get("/api/testas?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(testa.getId().intValue())))
            .andExpect(jsonPath("$.[*].name1").value(hasItem(DEFAULT_NAME_1.toString())));
    }

    @Test
    @Transactional
    public void getTesta() throws Exception {
        // Initialize the database
        testaRepository.saveAndFlush(testa);

        // Get the testa
        restTestaMockMvc.perform(get("/api/testas/{id}", testa.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(testa.getId().intValue()))
            .andExpect(jsonPath("$.name1").value(DEFAULT_NAME_1.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTesta() throws Exception {
        // Get the testa
        restTestaMockMvc.perform(get("/api/testas/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTesta() throws Exception {
        // Initialize the database
        testaRepository.saveAndFlush(testa);
        int databaseSizeBeforeUpdate = testaRepository.findAll().size();

        // Update the testa
        Testa updatedTesta = testaRepository.findOne(testa.getId());
        updatedTesta
            .name1(UPDATED_NAME_1);
        TestaDTO testaDTO = testaMapper.toDto(updatedTesta);

        restTestaMockMvc.perform(put("/api/testas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(testaDTO)))
            .andExpect(status().isOk());

        // Validate the Testa in the database
        List<Testa> testaList = testaRepository.findAll();
        assertThat(testaList).hasSize(databaseSizeBeforeUpdate);
        Testa testTesta = testaList.get(testaList.size() - 1);
        assertThat(testTesta.getName1()).isEqualTo(UPDATED_NAME_1);
    }

    @Test
    @Transactional
    public void updateNonExistingTesta() throws Exception {
        int databaseSizeBeforeUpdate = testaRepository.findAll().size();

        // Create the Testa
        TestaDTO testaDTO = testaMapper.toDto(testa);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restTestaMockMvc.perform(put("/api/testas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(testaDTO)))
            .andExpect(status().isCreated());

        // Validate the Testa in the database
        List<Testa> testaList = testaRepository.findAll();
        assertThat(testaList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteTesta() throws Exception {
        // Initialize the database
        testaRepository.saveAndFlush(testa);
        int databaseSizeBeforeDelete = testaRepository.findAll().size();

        // Get the testa
        restTestaMockMvc.perform(delete("/api/testas/{id}", testa.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Testa> testaList = testaRepository.findAll();
        assertThat(testaList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Testa.class);
        Testa testa1 = new Testa();
        testa1.setId(1L);
        Testa testa2 = new Testa();
        testa2.setId(testa1.getId());
        assertThat(testa1).isEqualTo(testa2);
        testa2.setId(2L);
        assertThat(testa1).isNotEqualTo(testa2);
        testa1.setId(null);
        assertThat(testa1).isNotEqualTo(testa2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TestaDTO.class);
        TestaDTO testaDTO1 = new TestaDTO();
        testaDTO1.setId(1L);
        TestaDTO testaDTO2 = new TestaDTO();
        assertThat(testaDTO1).isNotEqualTo(testaDTO2);
        testaDTO2.setId(testaDTO1.getId());
        assertThat(testaDTO1).isEqualTo(testaDTO2);
        testaDTO2.setId(2L);
        assertThat(testaDTO1).isNotEqualTo(testaDTO2);
        testaDTO1.setId(null);
        assertThat(testaDTO1).isNotEqualTo(testaDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(testaMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(testaMapper.fromId(null)).isNull();
    }
}
