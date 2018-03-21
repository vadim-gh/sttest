package test.transfer.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import test.transfer.service.TransferService;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
public class TransferRestController implements Serializable {

    static final String AMOUNT_FORMAT = "0.##";
    private static final Logger LOGGER = Logger.getLogger(TransferRestController.class.getCanonicalName());
    private final TransferService transferService;

    @Autowired
    public TransferRestController(final TransferService transferService) {
        this.transferService = transferService;
    }

    @RequestMapping(value = "/transfer", method = RequestMethod.GET)
    public ResponseEntity<?> transfer(@RequestParam(value = "accountCodeFrom", required = false) final String accountCodeFrom,
                                      @RequestParam(value = "accountCodeTo", required = false) final String accountCodeTo,
                                      @RequestParam(value = "amount", required = false) final String amountStr) {
        try {
            final BigDecimal amount;
            if (amountStr == null) {
                amount = null;
            } else {
                try {
                    final DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    decimalFormat.applyLocalizedPattern(AMOUNT_FORMAT);
                    decimalFormat.setParseBigDecimal(true);
                    amount = (BigDecimal) decimalFormat.parse(amountStr);
                } catch (final ParseException e) {
                    return new ResponseEntity<>("amount must match format '" + AMOUNT_FORMAT + "'",
                            HttpStatus.BAD_REQUEST);
                }
            }
            try {
                transferService.transfer(accountCodeFrom, accountCodeTo, amount);
                return new ResponseEntity<>("ok", HttpStatus.OK);
            } catch (final IllegalArgumentException e) {
                LOGGER.finer(e.getMessage());
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } catch (final Throwable t) {
            LOGGER.log(Level.SEVERE, t.getMessage(), t);
            return new ResponseEntity<>("unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
