package ee.valiit.back_toytrade.trade;

import ee.valiit.back_toytrade.domain.category.Category;
import ee.valiit.back_toytrade.domain.category.CategoryService;
import ee.valiit.back_toytrade.domain.city.City;
import ee.valiit.back_toytrade.domain.city.CityMapper;
import ee.valiit.back_toytrade.domain.city.CityService;
import ee.valiit.back_toytrade.domain.condition.Condition;
import ee.valiit.back_toytrade.domain.condition.ConditionMapper;
import ee.valiit.back_toytrade.domain.condition.ConditionService;
import ee.valiit.back_toytrade.domain.toy.Toy;
import ee.valiit.back_toytrade.domain.toy.toy_transaction.*;
import ee.valiit.back_toytrade.domain.toy.toy_transaction.terminal.Terminal;
import ee.valiit.back_toytrade.domain.toy.toy_transaction.terminal.TerminalService;
import ee.valiit.back_toytrade.domain.user.User;
import ee.valiit.back_toytrade.domain.user.UserMapper;
import ee.valiit.back_toytrade.domain.user.UserService;
import ee.valiit.back_toytrade.trade.dto.CategoryDto;
import ee.valiit.back_toytrade.trade.dto.CityDto;
import ee.valiit.back_toytrade.trade.dto.ConditionDto;
import ee.valiit.back_toytrade.trade.dto.ToyDto;
import ee.valiit.back_toytrade.domain.toy.ToyMapper;
import ee.valiit.back_toytrade.domain.toy.ToyService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TradeService {

    @Resource
    private ToyService toyService;

    @Resource
    private ToyMapper toyMapper;

    @Resource
    private CityMapper cityMapper;

    @Resource
    private UserService userService;

    @Resource
    private ConditionMapper conditionMapper;

    @Resource
    private CityService cityService;

    @Resource
    private ConditionService conditionService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private ToyTransactionService toyTransactionService;

    @Resource
    private ToyTransactionMapper toyTransactionMapper;

    @Resource
    private TerminalService terminalService;
    private final ToyTransactionRepository toyTransactionRepository;

    public TradeService(ToyTransactionRepository toyTransactionRepository) {
        this.toyTransactionRepository = toyTransactionRepository;
    }

    public List<ToyDto> getAllToys() {
        List<Toy> toys = toyService.findActiveListedToys();
        return toyMapper.toDtos(toys);
    }


    public void addNewToy(ToyDto toyDto) {
        Toy toy = toyMapper.toEntity(toyDto);
        User user = userService.findUser(toyDto.getUserId());
        toy.setUser(user);
        Optional<City> cityById = cityService.findCityById(toyDto.getCityId());
        toy.setCity(cityById.get());
        Optional<Condition> conditionById = conditionService.findConditionById(toyDto.getConditionId());
        toy.setCondition(conditionById.get());
        Optional<Category> categoryById = categoryService.findCategoryById(toyDto.getCategoryId());
        toy.setCategory(categoryById.get());

        toyService.addNewToy(toy);
    }
    public List<ToyDto> getToysByCategories(List<CategoryDto> categoryDtos) {
        List<Toy> toys = getToys(categoryDtos);
        return toyMapper.toDtos(toys);
    }

    private List<Toy> getToys(List<CategoryDto> categoryDtos) {
        List<Toy> toys = new ArrayList<>();
        for (CategoryDto dto : categoryDtos) {
            Integer categoryId = dto.getCategoryId();
            if (dto.getIsSelected()) {
                toys.addAll(toyService.findActiveListedToys(categoryId));
            }
        }
        return toys;
    }
    public List<ToyDto> getMyToys(Integer userId) {
        List<Toy> toys = toyService.findToys(userId);
        return toyMapper.toDtos(toys);
    }

    public Toy findToy(Integer toyId) {
        return toyService.findToy(toyId);

    }

    public List<ConditionDto> getAllConditions() {
        List<Condition> allConditions = conditionService.getAllConditions();
        return conditionMapper.toDtos(allConditions);

    }

    public List<CityDto> getAllCities() {
        List<City> allCities = cityService.getAllCities();
        return cityMapper.toDtos(allCities);
    }
    public Integer getMyPoints(Integer userId) {
        return userService.findUser(userId).getPoints();
    }

    public void addNewTransaction(ToyTransactionRequest toyTransactionRequest) {
        Toy toy = toyService.findToy(toyTransactionRequest.getToyId());
        User buyer = userService.findUser(toyTransactionRequest.getBuyerId());
        Terminal terminal = terminalService.findTerminal(toyTransactionRequest.getTerminalId());
        ToyTransaction toyTransaction = createToyTransaction(toy, buyer, terminal);
        toyTransactionService.saveToyTransaction(toyTransaction);
    }

    private static ToyTransaction createToyTransaction(Toy toy, User buyer, Terminal terminal) {
        ToyTransaction toyTransaction = new ToyTransaction();
        toyTransaction.setToy(toy);
        toyTransaction.setSeller(toy.getUser());
        toyTransaction.setBuyer(buyer);
        toyTransaction.setTerminal(terminal);
        toyTransaction.setStatus(Status.WANTED);
        return toyTransaction;
    }

    public void setTransactionStatusSent(Integer toyTransactionId) {
        ToyTransaction toytransaction = toyTransactionService.findById(toyTransactionId);
        toytransaction.setStatus(Status.SENT);
        toyTransactionService.saveToyTransaction(toytransaction);
    }
    public void setTransactionStatusCompleted(Integer toyTransactionId) {
        ToyTransaction toyTransaction = toyTransactionService.findById(toyTransactionId);
        User buyer = userService.findUser(toyTransaction.getBuyer().getId());
        User seller = userService.findUser(toyTransaction.getSeller().getId());
        buyer.setPoints(buyer.getPoints() - 1);
        seller.setPoints(seller.getPoints() + 1);
        toyTransaction.setStatus(Status.COMPLETED);
        toyTransactionService.saveToyTransaction(toyTransaction);
    }

    public List<ToyTransactionDto> findTransactions(Integer userId) {
//        User user = userService.findUser(userId);
        List<ToyTransaction> toyTransactions = toyTransactionService.findUserTransactions(userId);
        return toyTransactionMapper.toDtos(toyTransactions);
    }
}
