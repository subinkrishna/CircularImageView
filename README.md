CircularImageView
====
Android ImageView implementation that draws circular images with support for optional placeholder text.

![cat](art/cat.png)

### Usage

**XML:**

````xml
<com.subinkrishna.widget.CircularImageView
    android:id="@+id/image"
    android:layout_width="50dp"
    android:layout_height="50dp"
    android:src="@drawable/c2"
    app:borderWidth="2dp"
    app:borderColor="@android:color/white"
    app:placeholderBackgroundColor="@android:color/black"
    app:placeholderText="CV"
    app:placeholderTextSize="22sp"
    app:placeholderTextColor="@android:color/white" />
````

**Java:**

````java
CircularImageView imageView = findViewById(R.id.image);
imageView.setBorderColor(Color.WHITE);
imageView.setBorderWidth(TypedValue.COMPLEX_UNIT_DIP, 2);
imageView.setPlaceholder("CV", Color.BLACK, Color.WHITE);
imageView.setPlaceholderTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
````

**Custom Attributes**

* `borderWidth` (default: `0`)
* `borderColor` (default: `#FFFFFFFF`)
* `placeholderText`
* `placeholderTextSize` (default: `0`)
* `placeholderTextColor` (default: `#FF000000`)
* `placeholderBackgroundColor` (default: `#FFDDDDDD`)

**Java Methods**

* `setBorderWidth(int unit, int size)`
* `setBorderColor(@ColorInt int color)`
* `setPlaceholder(String text)`
* `setPlaceholder(String text, @ColorInt int backgroundColor, @ColorInt int textColor)`
* `setPlaceholderTextSize(int unit, int size)`

**Extending CircularImageView**

* `formatPlaceholderText(String text)`: override to customize the formatting of placeholder text. The default implementation picks the first two characters from the supplied text. (**NOTE**: `CircularImageView` supports only single line placeholders.)
* `getTextPaint()`: override to change the `Paint` used to draw the placeholder text.

### Download

Download the latest version from [releases][1].

## License

    Copyright (C) 2015 Subinkrishna Gopi

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[1]: ../../releases