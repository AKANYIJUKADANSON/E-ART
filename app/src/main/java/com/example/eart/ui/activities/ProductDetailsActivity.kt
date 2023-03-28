package com.example.eart.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.eart.R
import com.example.eart.baseactivity.BaseActivity
import com.example.eart.firestore.FirestoreClass
import com.example.eart.modules.CartItem
import com.example.eart.modules.Constants
import com.example.eart.modules.GlideLoader
import com.example.eart.modules.PrdctDtlsClass
import kotlinx.android.synthetic.main.activity_product_details.*

class ProductDetailsActivity : BaseActivity(), View.OnClickListener {
    /**
     * mProductId, global variable will be initialized as soon as we get into this activity
     * after its value has been sent from the productsAdapter holderItemClickListener
     */
    private var  mProdcutId:String = ""
    /**
     *  Creating an object that will contain the item and is of type prdctclass
     *  The object will be best initialized when we download the product from firestore
     *  ie productDownloadSuccess()
    */
    private var productExraOwnerId:String = ""
    private lateinit var mProductDetails:PrdctDtlsClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)



        // Checking if there is any intent with extra information
        /**
         * First checking if the intent has the extra data including the product(document) id
         * for the specific product to display and this will help to assign this id to the global
         * variable mProductId
         */
        if(intent.hasExtra(Constants.PRODUCT_EXTRA_ID)){
            // Store the product id in the mProductId global variable
            mProdcutId = intent.getStringExtra(Constants.PRODUCT_EXTRA_ID)!!
        }

        if(intent.hasExtra(Constants.PRODUCT_EXTRA_OWNER_ID)){
            // Store the set the userid of the product that is not ours
            productExraOwnerId = intent.getStringExtra(Constants.PRODUCT_EXTRA_OWNER_ID)!!
        }

        /**
         * Comparing if the product is for the current logged in user
         * If ts ours then we set the "add_to_cart" & " go_to_cart" btns invisible
         * and visible if the product is not for the current logged in user
        */
        if (FirestoreClass().getCurrentUserID() == productExraOwnerId){
            add_to_cart_btn.visibility = View.GONE
            go_to_cart_btn.visibility = View.GONE
        }else{
            add_to_cart_btn.visibility = View.VISIBLE
        }


        setUpActionBar()
        add_to_cart_btn.setOnClickListener(this)
        go_to_cart_btn.setOnClickListener(this)

        // Getting extra product details
        getExtraProductDetails()
    }

    //    Setting the action bar
    private fun setUpActionBar() {
        val productDetailsToolbar = findViewById<Toolbar>(R.id.toolbar_product_details_activity)
        setSupportActionBar(productDetailsToolbar)

        val actionbar = supportActionBar
        if(actionbar != null){
            with(actionbar) {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.ic_back_arrow)
            }
        }

        productDetailsToolbar.setNavigationOnClickListener { onBackPressed() }
    }


    private fun getExtraProductDetails(){
        progressDialog("Loading...")
        FirestoreClass().getExtraProductDetailsFromFirestore(this, mProdcutId)
    }

    fun productDetailsDownloadSuccess(product:PrdctDtlsClass){


        /**
         * initializing the mProductDetails variable assigning it the product that was downloaded
         * the "product" is an object of an item that is retrieved from cloud and passed to  this function
         * from the "getExtraProductDetailsFromFirestore" function in firestoreclass
         */
        mProductDetails = product

        // Assign the details to the views in the product details layout
        GlideLoader(this).loadProductPicture(product.image, iv_product_detail_image)
        tv_product_details_title.text = product.productTitle
        tv_product_details_price.text = "$${product.productPrice}"
        tv_product_details_description.text = product.prodctDescrptn
        tv_product_details_stock_quantity.text = product.stock_quantity
        tv_product_details_stock_quantity.setTextColor(
            ContextCompat.getColor(
                this, R.color.green
            )
        )

        /**
         * Check if the stock quantity is 0 or less and avoid the customer fro adding it to cart
         * by hiding the add to cart btn
         * And then change the stock quantity to "OUT OF STOCK"
         */
        if(product.stock_quantity.toInt() == 0){
            hideProgressDialog()
            add_to_cart_btn.visibility = View.GONE
            tv_product_details_stock_quantity.text = "OUT OF STOCK"
            tv_product_details_stock_quantity.setTextColor(
                ContextCompat.getColor(
                    this, R.color.errorColor
                )
            )
        }else{
            /**
             * after the putextra data has been obtained from cloud using the id
             * First Check if the current user id is  same as the user id on the product / if
             * the product belongs to curre user
             * else check if the product exists in cart....  if it exists then hide progdialog
             */
            if (FirestoreClass().getCurrentUserID() == product.user_id){
                // Hide the progress dialog
                hideProgressDialog()
            }else{
                FirestoreClass().checkIfItemExistInCart(this, mProdcutId)
            }
        }
    }


    // Creating the function that adds an item to cart
    private fun addItemToCart(){
        // creating an object
        val cartItem = CartItem(
            FirestoreClass().getCurrentUserID(),
            mProdcutId,
            mProductDetails.productTitle,
            mProductDetails.productPrice,
            mProductDetails.image,
            Constants.DEFAULT_ITEM_QUANTITY
        )

        progressDialog("Adding...")
        // Adding the object containing the product details into the cart
        FirestoreClass().addItemToFirestoreCart(this, cartItem)
    }

    // The function to run after product is successfully added to cart
    fun addToCartSuccess(){
        hideProgressDialog()
        Toast.makeText(this,
            resources.getString(R.string.add_To_Cart_Success),
            Toast.LENGTH_LONG
        ).show()

        // If a product is added to cart then the go_to_cart btn should be visible and
        // the add_to_cart btn gone
        add_to_cart_btn.visibility = View.GONE
        go_to_cart_btn.visibility = View.VISIBLE
    }

    // if the product exists in cart already then hide the button to add it again
    fun productExistsInCart(){
        hideProgressDialog()
        // make the visibility of add_to_cart btn GONE and that of go_to_cart VISIBLE
        add_to_cart_btn.visibility = View.GONE
        go_to_cart_btn.visibility = View.VISIBLE
    }


    override fun onClick(v: View?) {
        // Creating an object for the cart
        if(v != null){
            when(v.id){
                R.id.add_to_cart_btn ->{
                    //Add product to cart
                    addItemToCart()
                }

                R.id.go_to_cart_btn ->{
                    startActivity(Intent(this, CartListActivity::class.java))
                }
            }
        }

    }


}