//
//   Copyright 2018  SenX S.A.S.
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
//

package io.warp10.script.functions;

import io.warp10.continuum.gts.GTSHelper;
import io.warp10.continuum.gts.GeoTimeSerie;
import io.warp10.script.NamedWarpScriptFunction;
import io.warp10.script.WarpScriptStackFunction;
import io.warp10.script.WarpScriptException;
import io.warp10.script.WarpScriptStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Detect patterns in GTS instances
 */
public class PATTERNDETECTION extends NamedWarpScriptFunction implements WarpScriptStackFunction {
  
  private final boolean standardizePAA;
  
  public PATTERNDETECTION(String name, boolean standardizePAA) {
    super(name);
    this.standardizePAA = standardizePAA;
  }
  
  @Override
  public Object apply(WarpScriptStack stack) throws WarpScriptException {
    
    Object top = stack.pop();
    
    if (!(top instanceof Number)) {
      throw new WarpScriptException(getName() + " expects a quantization scale on top of the stack.");
    }
        
    int alphabetSize = ((Number) top).intValue();

    top = stack.pop();
    
    if (!(top instanceof Number)) {      
      throw new WarpScriptException(getName() + " expects a pattern length at the second level of the stack.");
    }
    
    int wordLen = ((Number) top).intValue();
    
    top = stack.pop();
    
    if (!(top instanceof Number)) {
      throw new WarpScriptException(getName() + " expects a window size at the third level of the stack.");
    }
    
    int windowLen = ((Number) top).intValue();
            
    top = stack.pop();
    
    if (!(top instanceof List)) {
      throw new WarpScriptException(getName() + " expects a list of patterns as the fourth level of the stack.");
    }
    
    List<String> patterns = (List<String>) top;
    
    top = stack.pop();
    
    if (top instanceof GeoTimeSerie) {
      stack.push(GTSHelper.detect((GeoTimeSerie) top, alphabetSize, wordLen, windowLen, patterns, standardizePAA));
    } else if (top instanceof List) {
      List<GeoTimeSerie> series = new ArrayList<GeoTimeSerie>();
      
      for (Object o: (List<Object>) top) {
        if (! (o instanceof GeoTimeSerie)) {
          stack.push(top);
          throw new WarpScriptException(getName() + " can only operate on geo time serie instances.");
        }
        series.add(GTSHelper.detect((GeoTimeSerie) o, alphabetSize, wordLen, windowLen, patterns, standardizePAA));
      }
      stack.push(series);
    } else {
      stack.push(top);
      throw new WarpScriptException(getName() + " can only operate on geo time serie instances.");
    }
    
    return stack;
  }
}
