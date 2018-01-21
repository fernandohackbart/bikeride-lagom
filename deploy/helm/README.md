# Creating the helm for bikeride-backend

https://github.com/kubernetes/helm/blob/master/docs/chart_template_guide/getting_started.md

https://github.com/kubernetes/helm/blob/master/docs/charts_tips_and_tricks.md


To create the package
```
helm create bikeride-backend
tree bikeride-backend
```

To validate if the descriptors are OK:

```
helm install -n test --debug --dry-run bikeride-authentication
helm lint --strict bikeride-athentication
```


```
helm create bikeride-authentication
helm install -n test bikeride-authentication
helm delete --purge test
```

To clone an existing chart and just replacing the values
```
find . -type f -exec sed -i 's/authentication/ride/g' {} \;
```
