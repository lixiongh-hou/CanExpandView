### CanExpandView
CanExpandTextView


[![](https://jitpack.io/v/lixiongh-hou/CanExpandView.svg)](https://jitpack.io/#lixiongh-hou/CanExpandView)

## 引入项目

```java
implementation 'com.github.lixiongh-hou:CanExpandView:最新版本'
```
```java
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

xml使用 两个TextView的id必须定义
 
 ```java
  <com.example.canexpandview.CanExpandTextView
        android:id="@+id/canExpandTextView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:animal_duration="300"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:max_expend_lines="4">

        <TextView
            android:id="@id/visible_context_tv"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:lineSpacingExtra="5dp"
            android:textColor="#000000"
            android:textSize="14sp" />

        <TextView
            android:id="@id/click_expand_tv"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="3dp"
            android:textColor="#ff576b95"
            android:textSize="14sp" />
    </com.example.canexpandview.CanExpandTextView>
 ```
 ## 属性

|            name             |  format   |   description    |
| :-------------------------: | :-------: | :--------------: |
|         max_expend_lines    |  integer  |     默认显示行数 |
|       animal_duration       | integer   |     动画执行时间  |

    
