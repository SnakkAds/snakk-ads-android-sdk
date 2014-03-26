package com.snakk.adview;

import java.util.Map;

interface IMraidCommand {
  public void execute(Map<String, String> params, AdViewCore adView);
}
