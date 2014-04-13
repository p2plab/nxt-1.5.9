package nxt.http;

import nxt.Account;
import nxt.NxtException;
import nxt.util.Convert;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public final class GetAccount extends APIServlet.APIRequestHandler {

    static final GetAccount instance = new GetAccount();

    private GetAccount() {
        super("account");
    }

    @Override
    JSONStreamAware processRequest(HttpServletRequest req) throws NxtException {

        Account account = ParameterParser.getAccount(req);

        JSONObject response = JSONData.accountBalance(account);

        synchronized (account) {
            if (account.getPublicKey() != null) {
                response.put("publicKey", Convert.toHexString(account.getPublicKey()));
            }
            if (account.getName() != null) {
                response.put("name", account.getName());
            }
            if (account.getDescription() != null) {
                response.put("description", account.getDescription());
            }

            JSONArray assetBalances = new JSONArray();
            for (Map.Entry<Long, Long> assetBalanceEntry : account.getAssetBalancesQNT().entrySet()) {

                JSONObject assetBalance = new JSONObject();
                assetBalance.put("asset", Convert.toUnsignedLong(assetBalanceEntry.getKey()));
                assetBalance.put("balanceQNT", String.valueOf(assetBalanceEntry.getValue()));
                assetBalances.add(assetBalance);

            }
            if (assetBalances.size() > 0) {
                response.put("assetBalances", assetBalances);
            }

            JSONArray unconfirmedAssetBalances = new JSONArray();
            for (Map.Entry<Long, Long> unconfirmedAssetBalanceEntry : account.getUnconfirmedAssetBalancesQNT().entrySet()) {

                JSONObject unconfirmedAssetBalance = new JSONObject();
                unconfirmedAssetBalance.put("asset", Convert.toUnsignedLong(unconfirmedAssetBalanceEntry.getKey()));
                unconfirmedAssetBalance.put("unconfirmedBalanceQNT", String.valueOf(unconfirmedAssetBalanceEntry.getValue()));
                unconfirmedAssetBalances.add(unconfirmedAssetBalance);

            }
            if (unconfirmedAssetBalances.size() > 0) {
                response.put("unconfirmedAssetBalances", unconfirmedAssetBalances);
            }

        }
        return response;
    }

}
