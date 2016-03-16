# SwingTree
Constructs a tree of the components in a swing application

<a href="http://www.youtube.com/watch?feature=player_embedded&v=I1kp_bgHq4Q
" target="_blank"><img src="http://img.youtube.com/vi/I1kp_bgHq4Q/0.jpg" 
alt="Swing Tree by Discovery" title="Swing Tree by Discovery" width="240" height="180" border="10" /></a>

You can add this repository to your Java Project using [JitPack](https://jitpack.io/).

#### Example

```java
import java.io.FileWriter;
import java.io.IOException
import com.github.luskjh.SwingTree;

/* We're using the automaton library (https://github.com/renatoathaydes/Automaton) 
 * for its GUI selectors
 */
import com.athaydes.automaton.Swinger;

...

/* Here we're using reflection to get the main class of the swing application */
Class<?> mainClass = Class.forName("package.then.classname");
Method method = mainClass.getDeclaredMethod("main", String[].class);
        
/* Start swing application */
String[] params = {};
method.invoke(null, (Object) params);

swinger = Swinger.forSwingWindow();

/* Give time for application to start */
swinger.pause(500);

Component frame = swinger.getAt("type:FrameClassName");

/* assemble tree */
SwingTree st = new SwingTree(frame);

/* Get JSON - currently the only output format supported */
JSONObject obj = st.toJSON();

/* Write to file */
try (FileWriter file = new FileWriter("/path/to/file.json")) {
    file.write(obj.toJSONString());
} catch (IOException e) {
    System.out.println("Error writing JSON to file");
}
```



